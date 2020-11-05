package interface_;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import bean.User;
/**
 * 操作用户的接口
 * @author ljl
 *
 */
public interface UserInterface extends Remote{
	/**
	 * 按用户名查找用户
	 * @param name 用户名
	 * @return 用户的实例
	 * @throws RemoteException
	 */
	public User getUserByName(String name)throws RemoteException;
	/**
	 * 添加用户，并进行重名检测
	 * @param user 用户的实例
	 * @return 是否添加成功
	 * @throws RemoteException
	 */
	public boolean addUser(User user)throws RemoteException;
	/**
	 * 额外添加的方法，方便操作：获取用户列表
	 * @return
	 * @throws RemoteException
	 */
	public ArrayList<User> getUserList()throws RemoteException;
	
}
