/**
 * 
 */
package myImplement;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import bean.User;
import myInterface.UserInterface;
/**
 * UserInterface的实现类
 * @author ljl
 *
 */
public class UserImplement extends UnicastRemoteObject implements UserInterface{

	private static final long serialVersionUID = 1L;
	private ArrayList<User> userList=new ArrayList<User>();//用户列表
	/**
	 * 无参构造函数，必须存在，且抛出异常RemoteException
	 * @throws RemoteException
	 */
	public UserImplement() throws RemoteException{
//		System.out.println("UserImplement--OK");
	}
	@Override
	public ArrayList<User> getUserList()throws RemoteException {
		return this.userList;//返回userList
	}
	@Override
	public User getUserByName(String name) throws RemoteException {
		for(User user:userList){
			if(user.getName().equals(name)){//查找名字一样的User
				return user;//返回符合条件的User实例
			}
		}
		return null;//未找到相应用户，返回null
	}
	@Override
	public boolean addUser(User user) throws RemoteException {
		int tag=0;//标记，是否重名
		for(User i:userList){
			if(i.getName().equals(user.getName())){//找到相同用户名的用户
				tag=1;//标记置为1
				break;//退出循环
			}
		}
		if(tag==0){//标记为0，说明无重复
			userList.add(user);//在用户列表中添加用户
			System.out.println("新添加的用户"+user.toString());
			return true;//返回结果，添加成功
		}else{//标记为1，用户名重复
			return false;//返回结果，添加失败
		}
	}
}
