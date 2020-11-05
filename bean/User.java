package bean;
import java.io.Serializable;
/**
 * USER CLASS
 * @author ljl
 *
 */
public class User implements Serializable{

	private String name;//用户名
	private String password;//密码
	
	/**
	 * 无参构造函数
	 */
	public User(){
		
	}

	/**
	 * 有参构造函数
	 * @param name 用户
	 * @param password 密码 
	 */
	public User(String name, String password) {
		super();
		this.name = name;
		this.password = password;
		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "User [name=" + name + "]";
	}

	

}
