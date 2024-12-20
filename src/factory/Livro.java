package factory;

import observer.Observer;
import observer.UsuarioObserver;


public class Livro extends ItemBiblioteca{
    
    
    public Livro(String titulo, String autor, String tipo, String area) {
        super(autor, titulo, tipo, area); 
    }
    

    @Override
    public String getTipo() {
        return  tipo;    }

    @Override
    public void addObserver(Observer observer) {
        super.addObserver(observer);
    }
     public String getTitulo() {
        return titulo;
    }
    public String getAutor() {
        return autor;
    }
    public String getArea() {
        return area;
    }


    @Override
    public void addObserver(UsuarioObserver alunoObserver) {
        throw new UnsupportedOperationException("Unimplemented method 'addObserver'");
    }
}
