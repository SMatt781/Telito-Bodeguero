package ejer1;

public class Main {
    public static void main(String[] args) {

        //EJERCICIO BASICO
        //crear objeto
        Auto auto1 = new Auto();

        auto1.modelo = "Toyota Corolla";
        //asignandole directamente el valor no nos permite
        //auto1.precio = -1000;
        auto1.setPrecio(345000.6);
        //mostrar el modelo y precio
        System.out.println("Modelo: " + auto1.getModelo()+"\n"
                            +"Precio: " + auto1.getPrecio());

        //1. Crear una clase Person con atributos privados name y age. Usar getters y setters
    }
}
