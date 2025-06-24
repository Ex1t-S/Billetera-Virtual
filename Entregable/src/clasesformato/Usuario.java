package clasesformato;

public class Usuario {
	private int id;
	private int id_persona;
	private String email;
	private String password;
	private boolean acepta_terminos;
	
	public Usuario () {
		
	}
	public Usuario (int id, int id_persona, String email, String password, boolean acepta_terminos) {
		this.id = id;
		this.id_persona = id_persona;
		this.email = email;
		this.password = password;
		this.acepta_terminos = acepta_terminos;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public void setIdPersona(int id_persona) {
		this.id_persona = id_persona;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setAceptaTerminos(boolean acepta_terminos) {
		this.acepta_terminos = acepta_terminos;
	}
	public int getId () {
		return id;
	}
	public int getIdPersona () {
		return id_persona;
	}
	public String getEmail () {
		return email;
	}
	public String getPassword () {
		return password;
	}
	public boolean getAceptaTerminos () {
		return acepta_terminos;
	}
}