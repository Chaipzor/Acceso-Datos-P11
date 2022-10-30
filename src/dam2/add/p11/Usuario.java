package dam2.add.p11;


public class Usuario {
	private String nombre;
	private String pass;
	private int intentos;
	
	public Usuario(String nombre, String pass, int intentos) {
		super();
		this.nombre = nombre;
		this.pass = pass;
		this.intentos = intentos;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public int getIntentos() {
		return intentos;
	}
	public void setIntentos(int intentos) {
		this.intentos = intentos;
	}
	
	public static void nuevoIntento() {
		
	}
	
}
