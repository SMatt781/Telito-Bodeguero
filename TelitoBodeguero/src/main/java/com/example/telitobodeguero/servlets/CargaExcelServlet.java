package com.example.telitobodeguero.servlets;


import com.example.telitobodeguero.daos.MovimientoDao;
import com.example.telitobodeguero.daos.ProductoDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.apache.poi.ss.usermodel.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

@WebServlet("/CargaExcelServlet")
@MultipartConfig
public class CargaExcelServlet extends HttpServlet {

    // Para validar
    private final ProductoDao pdao = new ProductoDao();
    // Para insertar
    private final MovimientoDao mdao = new MovimientoDao();

    // Estructura simple para el JSP
    public static class FilaPreview {
        public int index;
        public String sku;
        public Integer zonaId;
        public LocalDate fecha;
        public Integer cantidad;
        public Integer loteId;
        public LocalDate fechaVenc;
        public String error; // null si OK
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/Almacen/cargaMasiva.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");
        if ("confirmar".equalsIgnoreCase(accion)) {
            confirmar(req, resp);
        } else {
            validar(req, resp);
        }
    }

    private void validar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<FilaPreview> preview = new ArrayList<>();
        boolean tieneErrores = false;

        try {
            int zonaId = Integer.parseInt(req.getParameter("zonaId"));

            Part filePart = req.getPart("archivo");
            if (filePart == null || filePart.getSize() == 0) {
                req.setAttribute("mensaje", "Adjunta un archivo .xlsx");
                req.getRequestDispatcher("/Almacen/cargaMasiva.jsp").forward(req, resp);
                return;
            }

            try (InputStream is = filePart.getInputStream();
                 Workbook wb = WorkbookFactory.create(is)) {

                Sheet sheet = wb.getSheetAt(0);
                int rowNum = 0;

                for (Row row : sheet) {
                    rowNum++;
                    if (rowNum == 1) continue; // encabezados

                    FilaPreview fp = new FilaPreview();
                    fp.index = rowNum;

                    fp.sku = getString(row, 0);
                    Integer zonaX = parseInt(row, 1);
                    // ignoramos la columna zona del Excel y usamos zonaId del form:
                    fp.zonaId = zonaId;

                    fp.fecha = parseDate(row, 2);
                    fp.cantidad = parseInt(row, 3);
                    fp.loteId = parseInt(row, 4);
                    fp.fechaVenc = parseDate(row, 5);

                    // Validaciones básicas
                    if (isBlank(fp.sku) || fp.zonaId == null || fp.fecha == null || fp.cantidad == null || fp.loteId == null) {
                        fp.error = "Campos obligatorios faltantes";
                    } else if (fp.cantidad <= 0) {
                        fp.error = "Cantidad debe ser > 0";
                    } else {
                        // Validaciones contra BD (SKU existe y lote pertenece)
                        try (var conn = pdao.getConnection()) {
                            Integer idProd = pdao.findProductoIdBySku(conn, fp.sku.trim());
                            if (idProd == null) {
                                fp.error = "SKU no existe";
                            } else if (!pdao.lotePerteneceAProducto(conn, fp.loteId, idProd)) {
                                fp.error = "Lote no pertenece al SKU";
                            }
                        } catch (Exception e) {
                            fp.error = "Error validando en BD: " + e.getMessage();
                        }
                    }

                    if (fp.error != null) tieneErrores = true;
                    preview.add(fp);
                }
            }

            // Guardamos el preview en sesión para el paso "confirmar"
            HttpSession ses = req.getSession();
            ses.setAttribute("previewExcel", preview);

            req.setAttribute("preview", preview);
            req.setAttribute("tieneErrores", tieneErrores);
            req.getRequestDispatcher("/Almacen/cargaMasiva.jsp").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("mensaje", "Error en validación: " + e.getMessage());
            req.getRequestDispatcher("/Almacen/cargaMasiva.jsp").forward(req, resp);
        }
    }

    private void confirmar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession ses = req.getSession(false);
        if (ses == null) {
            req.setAttribute("mensaje", "Sesión sin datos de validación.");
            req.getRequestDispatcher("/Almacen/cargaMasiva.jsp").forward(req, resp);
            return;
        }
        @SuppressWarnings("unchecked")
        List<FilaPreview> preview = (List<FilaPreview>) ses.getAttribute("previewExcel");
        if (preview == null) {
            req.setAttribute("mensaje", "No hay datos validados para confirmar.");
            req.getRequestDispatcher("/Almacen/cargaMasiva.jsp").forward(req, resp);
            return;
        }

        // Armamos las filas válidas para insertar
        List<MovimientoDao.FilaEntrada> filas = new ArrayList<>();
        Integer zonaId = null;
        for (FilaPreview fp : preview) {
            if (fp.error == null) {
                MovimientoDao.FilaEntrada f = new MovimientoDao.FilaEntrada();
                f.sku = fp.sku;
                f.loteId = fp.loteId;
                f.fecha = fp.fecha;
                f.cantidad = fp.cantidad;
                f.fechaVenc = fp.fechaVenc; // no crea lote; solo informativo
                filas.add(f);
                zonaId = fp.zonaId; // misma zona para todas (viene del form)
            }
        }

        int insertados = 0;
        try {
            if (zonaId == null) zonaId = 1;
            // Id de usuario (si no manejas sesión, pon 1 por simplicidad)
            Integer usuarioId = (Integer) req.getSession().getAttribute("usuarioId");
            if (usuarioId == null) usuarioId = 1;

            insertados = mdao.registrarEntradasMasivasSoloLotesExistentes(filas, zonaId, usuarioId, new ArrayList<>());

            // Limpia el preview para no reinsertar si recargan
            ses.removeAttribute("previewExcel");

            req.setAttribute("insertados", insertados);
            req.setAttribute("mensaje", "Operación completada.");
            req.getRequestDispatcher("/Almacen/cargaMasiva.jsp").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("insertados", insertados);
            req.setAttribute("mensaje", "Error en confirmación: " + e.getMessage());
            req.getRequestDispatcher("/Almacen/cargaMasiva.jsp").forward(req, resp);
        }
    }

    /* ===== Helpers para leer Excel ===== */

    private static String getString(Row r, int idx) {
        try {
            Cell c = r.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (c == null) return null;
            c.setCellType(CellType.STRING);
            String s = c.getStringCellValue();
            return (s == null || s.trim().isEmpty()) ? null : s.trim();
        } catch (Exception e) { return null; }
    }

    private static Integer parseInt(Row r, int idx) {
        try {
            Cell c = r.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (c == null) return null;
            if (c.getCellType() == CellType.NUMERIC) return (int)Math.round(c.getNumericCellValue());
            String s = c.getStringCellValue();
            return (s==null||s.trim().isEmpty()) ? null : Integer.parseInt(s.trim());
        } catch (Exception e) { return null; }
    }

    private static LocalDate parseDate(Row r, int idx) {
        try {
            Cell c = r.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (c == null) return null;
            if (c.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(c)) {
                return c.getLocalDateTimeCellValue().toLocalDate();
            } else {
                String s = c.getStringCellValue();
                if (s==null || s.trim().isEmpty()) return null;
                return LocalDate.parse(s.trim()); // YYYY-MM-DD
            }
        } catch (Exception e) { return null; }
    }

    private static boolean isBlank(String s){ return s==null || s.trim().isEmpty(); }
}
