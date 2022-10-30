package dam2.add.p11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class MainP11 {

	static ArrayList<Usuario> listadoUsuario = new ArrayList<Usuario>();
	final static int intentosTotales = 3;
	
	public static void main(String[] args) {

		int resultado = 0;
		
		leerFichero();
		
		do {
			resultado = login();
		} while (resultado == 0);
		
		if (resultado == 2)
			menuAdmin();
		
		System.out.println("Fin.");
		guardarDatos();
	}

	// Guardamos todos los datos en el archivo "acceso.txt"
	private static void guardarDatos() {
		int contador = 0;
		String separador = File.separator;
		File f = new File("." + separador + "acceso.txt");

		try {
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			do {
				// Obtenemos los datos de cada usuario
				String nombre = listadoUsuario.get(contador).getNombre();
				String pass = listadoUsuario.get(contador).getPass();
				int intentos = listadoUsuario.get(contador).getIntentos();

				// Escribimos los datos en el nuevo archivo
				bw.write(nombre + ":" + pass + ":" + intentos);
				bw.newLine();
				
				contador++;
			}while(listadoUsuario.size() > contador);
			bw.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error al abrir archivo.");
		} catch (IOException e) {
			System.out.println("Error E/S.");
		}
	}

	// En caso de ser el usuario "admin" pasará por este menu.
	private static void menuAdmin() {
		int contador = 0;
		boolean bloqueados = false;
		String nom;
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Usuarios bloqueados: ");
		
		// Comprueba si existen usuarios bloqueados.
		do {
			if( listadoUsuario.get(contador).getIntentos() == intentosTotales ) {
				System.out.println(listadoUsuario.get(contador).getNombre());
				bloqueados = true;
			}
			contador++;
		}while (listadoUsuario.size() > contador);
		
		if (!bloqueados) {
			System.out.println("No existen usuarios bloqueados.");
		} else {
			do {
				System.out.println("Introduce el nombre del usuario a desbloquear o 0 para salir.");
				nom = sc.nextLine();
				int contadorI = 0;
				if(!nom.equals("0")) {
					do {
						if(nom.equals(listadoUsuario.get(contadorI).getNombre()) && listadoUsuario.get(contadorI).getIntentos() == 3)
							listadoUsuario.get(contadorI).setIntentos(0);
						contadorI++;
					}while(listadoUsuario.size() > contadorI);
				}
			}while(!nom.equals("0"));
		}

	}

	private static void guardarUsuario(int id) {
		String separador = File.separator;
		File f = new File("." + separador + "acceso.txt");

		try {
			FileWriter fw = new FileWriter(f, true);
			BufferedWriter bw = new BufferedWriter(fw);

			String nombre = listadoUsuario.get(id).getNombre();

			String pass = listadoUsuario.get(id).getPass();

			bw.write(nombre + ":" + pass + ":" + 0);

			bw.newLine();

			bw.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error al abrir archivo.");
		} catch (IOException e) {
			System.out.println("Error E/S.");
		}
	}

	public static int login() {
		int contador = 0;
		boolean nuevoUsuario = true;
		int opc = 0;
		Scanner sc = new Scanner(System.in);

		System.out.println("Usuario: ");
		String usuario = sc.nextLine();

		System.out.println("Contraseña: ");
		String pass = sc.nextLine();

		do {

			String nom = listadoUsuario.get(contador).getNombre();
			String clave = listadoUsuario.get(contador).getPass();
			int historialIntentos = listadoUsuario.get(contador).getIntentos();
			
			// Comprueba si el usuario está bloqueado.
			if((nom.equals(usuario) && listadoUsuario.get(contador).getIntentos()==3)){
				System.out.println("Usuario bloqueado. Contacte con un administrador.");
				nuevoUsuario = false;
				
			} else {
				
				// Comprueba que nombre y clave coincidan.
				if(!(nom.equals(usuario) && clave.equals(pass))) {
					
					if (nom.equals(usuario)) {
						int intentos = listadoUsuario.get(contador).getIntentos();
						
						nuevoUsuario = false;
						intentos++;
						
						System.out.println("Contraseña incorrecta. Vuelva a introducir la contraseña.");
						System.out.println("Intentos: " + intentos + "/" + intentosTotales);
						
						listadoUsuario.get(contador).setIntentos(intentos);
						
						if(listadoUsuario.get(contador).getIntentos() ==3) {
							System.out.println("Usuario bloqueado. Contacte con un administrador.");
						}
					}
					
					// Al acceder con un usuario y contraseña se reinician los intentos a 0.
				} else {
					System.out.println("Hola " + nom);
					registrarLog(usuario,true);
					listadoUsuario.get(contador).setIntentos(0);
					
					if(nom.equals("admin"))
						return 2;
					else
						return 1;
				}
			}
			contador++;
		}while(listadoUsuario.size() > contador);
		
		registrarLog(usuario,false);
		
		if (nuevoUsuario){
			do {
				opc = altaUsuario(usuario);
			}while (opc != 0);
		}
		return 0;
	}

	private static int altaUsuario(String usuario) {
		int opc = 0;
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Introduzca contraseña para dar de alta al usuario: " + usuario);
		String pass1 = sc.nextLine();
		System.out.println("Repita la contraseña.");
		String pass2 = sc.nextLine();

		if (pass1.equals(pass2)) {
			Usuario nuevo = new Usuario(usuario,pass1,0);
			listadoUsuario.add(nuevo);
			guardarUsuario(listadoUsuario.size()-1);
			System.out.println("Usuario creado con éxito.");
			opc = 0;
		} else {
			System.out.println("Las contraseñas no coinciden.");
			System.out.println("1 - Introducir contraseña");
			System.out.println("0 - Salir");
			opc = Integer.parseInt(sc.nextLine());
		}
		return opc;
	}

	// Carga los datos del archivo txt en un ArrayList del sistema.
	public static void leerFichero() {
		String separador = File.separator;
		File f = new File("." + separador + "acceso.txt");
		String datos;
		
		try {
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			datos = br.readLine();
			
			while (datos != null) {
				if(!(datos.length()<2)){

					String[] partes = datos.split(":");
					String nombre = partes[0];
					String pass = partes[1];
					int intentos = Integer.parseInt(partes[2]);

					System.out.println("Nombre: " + nombre);
					System.out.println("Contraseña: " + pass);
					System.out.println("Intentos: " + intentos);

					Usuario datosUsuario = new Usuario(nombre, pass, intentos);

					listadoUsuario.add(datosUsuario);
				}
				datos = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error al abrir archivo.");
		} catch (IOException e) {
			System.out.println("Error E/S.");
		}
	}

	// Guarda el registro de actividades (Logins correctos o incorrectos) en un fichero txt.
	public static void registrarLog(String nombre, boolean success) {
		String separador = File.separator;
		File f = new File("." + separador + "login.log");

		try {
			FileWriter fw = new FileWriter(f, true);
			BufferedWriter bw = new BufferedWriter(fw);
			LocalDate fecha = LocalDate.now();
			LocalTime hora = LocalTime.now();

			bw.write(nombre + ":" + success + ":" + fecha + ":" + hora);

			bw.newLine();

			bw.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error al abrir archivo.");
		} catch (IOException e) {
			System.out.println("Error E/S.");
		}
	}

}
