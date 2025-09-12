package ejer1;

public class Auto {

    // atributos
    public String modelo;
    private double precio;

    // metodos


    public void setPrecio(double precio) {
        if(precio<=0){
            System.out.println("Precio inválido");
        }else{
            this.precio = precio;
        }
    }

    public double getPrecio() {
        return precio;
    }

    public String getModelo() {
        return modelo;
    }
}
