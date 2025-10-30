package com.example.telitobodeguero.servlets;


import com.example.telitobodeguero.beans.Bloque;
import com.example.telitobodeguero.beans.Lote;
import com.example.telitobodeguero.beans.Movimiento;
import com.example.telitobodeguero.beans.Zonas;
import com.example.telitobodeguero.daos.MovimientoDao;

import com.example.telitobodeguero.daos.ProductoDaoAlm;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
// REQUIERE APACHE POI
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.sql.Connection;
import java.sql.SQLException;


@WebServlet(name = "CargaMasivaServlet", value = "/CargaMasivaServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 5,      // 5 MB
        maxRequestSize = 1024 * 1024 * 10)  // 10 MB
public class CargaExcelServlet extends HttpServlet {

    private int getZonaId(HttpServletRequest req) {
        Object z = req.getSession().getAttribute("zonaIdActual");
        if (z instanceof Integer) return (Integer) z;
        try { return Integer.parseInt(String.valueOf(z)); } catch (Exception e) { return 1; }
    }

    // ===================================================================
    //                      GET (DESCARGA DE PLANTILLA)
    // ===================================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        int zonaId = getZonaId(request);
        String ctx = request.getContextPath();

        if ("descargarPlantilla".equals(accion)) {
            ProductoDaoAlm pDao = new ProductoDaoAlm();
            ArrayList<Movimiento> inventario = pDao.obtenerInventarioParaPlantilla(zonaId);

            // Generación del Excel
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Movimientos ENTRADA");

                // 1. Cabecera (Índices 5 al 8 son los datos internos)
                String[] headers = {"SKU", "NOMBRE_PRODUCTO", "STOCK_ACTUAL", "BLOQUE_CODIGO", "ZONA_NOMBRE",
                        "idProducto", "idZonas", "idBloque", "idLote", // IDs INTERNOS
                        "CANTIDAD", "FECHA(YYYY-MM-DD)"}; // DATOS A LLENAR
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                }

                // 2. Llenar Filas
                int rowNum = 1;
                for (Movimiento m : inventario) {
                    Row row = sheet.createRow(rowNum++);

                    // Col. 0 a 4 (Visuales)
                    row.createCell(0).setCellValue(m.getProducto().getSku());
                    row.createCell(1).setCellValue(m.getProducto().getNombre());
                    row.createCell(2).setCellValue(m.getCantidad());
                    row.createCell(3).setCellValue(m.getBloque().getCodigo());
                    row.createCell(4).setCellValue(m.getZona().getNombre());

                    // Col. 5 a 8 (IDs Internos)
                    row.createCell(5).setCellValue(m.getProducto().getIdProducto());
                    row.createCell(6).setCellValue(m.getZona().getIdZonas());
                    row.createCell(7).setCellValue(m.getBloque().getIdBloque());
                    row.createCell(8).setCellValue(m.getLote().getIdLote());

                    // Col. 10 (Fecha sugerida)
                    row.createCell(10).setCellValue(LocalDate.now().toString());
                }

                // 3. Configurar respuesta para descarga
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=\"Plantilla_Entrada_Masiva_" + zonaId + ".xlsx\"");

                try (OutputStream outputStream = response.getOutputStream()) {
                    workbook.write(outputStream);
                }
                return;

            } catch (Exception e) {
                e.printStackTrace();
                String msg = "error|Error al generar la plantilla: " + e.getMessage();
                response.sendRedirect(ctx + "/CargaMasivaServlet?statusMessage=" + URLEncoder.encode(msg, StandardCharsets.UTF_8));
                return;
            }
        }

        // Acción por defecto: Mostrar el formulario de subida
        RequestDispatcher view = request.getRequestDispatcher("/Almacen/cargaMasiva.jsp");
        view.forward(request, response);
    }

    // ===================================================================
    //                  POST (SUBIDA, VALIDACIÓN Y REGISTRO)
    // ===================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        int zonaId = getZonaId(request);
        String ctx = request.getContextPath();
        String mensaje = "";

        if ("validarYSubir".equals(accion)) {
            Part filePart = request.getPart("archivoExcel");

            if (filePart == null || filePart.getSize() == 0) {
                mensaje = "error|Debe seleccionar un archivo para subir.";
                response.sendRedirect(ctx + "/CargaMasivaServlet?statusMessage=" + URLEncoder.encode(mensaje, StandardCharsets.UTF_8));
                return;
            }

            List<Movimiento> movimientosACargar = new ArrayList<>();
            List<String> errores = new ArrayList<>();
            MovimientoDao movDao = new MovimientoDao();
            int filasProcesadas = 0;

            Connection validationConn = null;
            try {
                // Usar una conexión para la validación de espacio libre
                validationConn = movDao.getConnection();

                // Lógica de Parseo de Excel
                try (InputStream fileContent = filePart.getInputStream();
                     Workbook workbook = WorkbookFactory.create(fileContent)) {

                    Sheet sheet = workbook.getSheetAt(0);

                    for (Row row : sheet) {
                        if (row.getRowNum() == 0) continue; // Saltar cabecera

                        filasProcesadas++;

                        // Si la columna CANTIDAD (Índice 9) está vacía, ignorar la fila (el usuario la omitió)
                        Cell cellCantidad = row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        if (cellCantidad == null || cellCantidad.getCellType() == CellType.BLANK) {
                            continue;
                        }

                        try {
                            // Extraer IDs internos y datos de entrada
                            int idBloque = (int) row.getCell(7).getNumericCellValue(); // Índice 7
                            int idLote = (int) row.getCell(8).getNumericCellValue();   // Índice 8
                            String codigoBloque = row.getCell(3).getStringCellValue();

                            int cantidad = (int) Math.round(cellCantidad.getNumericCellValue()); // Índice 9

                            Cell cellFecha = row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // Índice 10
                            LocalDate fecha = null;

                            if (cellFecha != null) {
                                if (cellFecha.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cellFecha)) {
                                    fecha = cellFecha.getLocalDateTimeCellValue().toLocalDate();
                                } else {
                                    fecha = LocalDate.parse(cellFecha.getStringCellValue().trim());
                                }
                            }

                            // Crear Movimiento y llenar los datos internos
                            Movimiento m = new Movimiento();
                            m.setCantidad(cantidad);
                            m.setFecha(fecha);
                            m.setTipoMovimiento("IN");
                            Lote l =  new Lote();
                            l.setIdLote(idLote);
                            m.setLote(l);
                            Zonas z = new Zonas();
                            z.setIdZonas(zonaId);
                            m.setZona(z);
                            Bloque b = new Bloque();
                            b.setIdBloque(idBloque);

                            b.setCodigo(codigoBloque);
                            m.setBloque(b);

                            // Validar usando el nuevo método DAO
                            String validacionResultado = movDao.validarMovimientoEntradaMasiva(validationConn, m, row.getRowNum() + 1); // +1 para número de fila real

                            if ("ok".equals(validacionResultado)) {
                                movimientosACargar.add(m);
                            } else {
                                errores.add(validacionResultado); // Añadir el error específico de la fila
                            }

                        } catch (DateTimeParseException e) {
                            errores.add("• Fila " + (row.getRowNum() + 1) + ": Error de formato de fecha. Use YYYY-MM-DD o formato Excel de fecha.");
                        } catch (IllegalStateException e) {
                            errores.add("• Fila " + (row.getRowNum() + 1) + ": Error de lectura de datos (ej. texto en un campo numérico).");
                        } catch (Exception e) {
                            errores.add("• Fila " + (row.getRowNum() + 1) + ": Error al leer datos internos o genérico: " + e.getMessage());
                        }
                    }
                }

                // 🛑 FIN DE LAS VALIDACIONES
                if (!errores.isEmpty()) {
                    // Si hay errores, devolver la lista de errores al JSP
                    request.setAttribute("erroresCarga", errores);
                    request.setAttribute("cantMovimientosProcesados", filasProcesadas);
                    RequestDispatcher view = request.getRequestDispatcher("/Almacen/cargaMasiva.jsp");
                    view.forward(request, response);
                    return;
                }

                // 5. REGISTRO MASIVO (si no hubo errores)
                if (movimientosACargar.isEmpty()) {
                    mensaje = "error|No se encontraron movimientos válidos para registrar. Verifique que llenó las columnas CANTIDAD y FECHA.";
                } else {
                    String resultadoRegistro = movDao.registrarMovimientosEntradaMasiva(movimientosACargar, zonaId);

                    if ("ok".equals(resultadoRegistro)) {
                        mensaje = "success|Carga masiva exitosa. Se registraron " + movimientosACargar.size() + " movimientos de ENTRADA.";
                    } else {
                        mensaje = "error|Error crítico al registrar en base de datos: " + resultadoRegistro;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                mensaje = "error|Error al procesar el archivo o de conexión: " + e.getMessage();
            } finally {
                if (validationConn != null) {
                    try { validationConn.close(); } catch (SQLException ignore) {}
                }
            }
        } else {
            mensaje = "error|Acción no reconocida.";
        }

        response.sendRedirect(ctx + "/CargaMasivaServlet?statusMessage=" + URLEncoder.encode(mensaje, StandardCharsets.UTF_8));
    }
}

