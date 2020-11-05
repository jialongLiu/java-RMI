package client;

import myInterface.MeetInterface;
import myInterface.UserInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import bean.Meet;
import bean.User;

/**
 * 远程客户端
 * 
 * @author ljl
 *
 */
// delete:ljl,520,0
// clear:ljl,520


public class Client {

	static Meet meeting;// 会议
	static int meetingID;
	static User user;// 用户
	static MeetInterface meetingInterface;// 操作会议的接口
	static UserInterface userInterface;// 操作用户的接口
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-hh:mm");// 格式化日期
	static int i =0;

	public static void main(String[] args)
			throws RemoteException, MalformedURLException, NotBoundException, ParseException {
		/**
		 * Step 1:查询远程对象
		 */
		meetingInterface = (MeetInterface) Naming.lookup("MeetingRemote");
		userInterface = (UserInterface) Naming.lookup("UserRemote");
		/**
		 * Step 2:获取输入，根据用户名和密码创建客户端
		 */
		String name;// 用户名
		String password;// 密码
		Scanner in = new Scanner(System.in);
		while (true) {
			if(args== null||args.length<1){
				System.out.println("输入用户名");
				name = in.nextLine();
				System.out.println("输入密码");
				password = in.nextLine();
			}else{
				name=args[0];
				password=args[1];
			}
			
			user = new User(name, password);// 创建用户
			boolean success = userInterface.addUser(user);// 添加用户
			if (success) {// 添加成功
				break;// 退出循环
			} else {// 添加失败（当用户名重复时出错）
				if(args!= null && args.length >=1){
					args[0]="default"+i;
					i++;
					System.err.println("重名错误,已使用默认用户名："+args[0]);
				}
				System.err.println("重名错误");
			}
		}
		/**
		 * Step 3:检测用户的数量，用户数量大于1时跳出循环
		 */
		int falg = 0;// 确定是否给出提示的标记
		while (true) {
			if (userInterface.getUserList().size() > 1) {// 用户数量大于1
				break;// 退出循环
			} else {// 用户数量仅有1个
				if (falg == 0) {// 第一次检测到只有一个用户，给出提示
					System.out.println("目前系统中只有一个用户，请再次启动客户端进行注册新用户！");
					falg = 1;// 标记：之后的循环不再提示
				}
			}
		}
		/**
		 * Step 4:本次启动成功，打印菜单
		 */
		System.out.println("客户端启动成功");
		printIntroduction();// 输出菜单
		/**
		 * Step 5:获取输入，处理命令
		 */
		String line = in.nextLine();
		int tag = handle(line);// 标记是否退出程序
		while (true) {
			if (tag == 1)
				break;// 用户输入quit时，标记置为1，退出循环，程序结束
			line = in.nextLine();// 继续获取用户输入
			tag = handle(line);// 获取标记
		}
	}

	/**
	 * 处理用户命令
	 * 
	 * @param line 用户命令
	 * @return 标记是否退出程序（0位继续执行程序，1位退出循环）
	 * @throws RemoteException
	 * @throws ParseException
	 */
	public static int handle(String line) throws RemoteException, ParseException {
		if (line.equals("help")) {// 输入为help时打印菜单
			printIntroduction();
			return 0;
		} else if (line.equals("quit")) {// 输入为quit时程序结束
			System.out.println("Bye~~~~~");
			return 1;
		} else if (line.equals("clear")) {// 输入为clear时清空会议记录
			meetingInterface.clearMeeting();// 调用远程方法
			System.out.println("Clear successful！");
			return 0;
		} else if (line.startsWith("add")) {// 输入为add时添加会议
			addMeeting(line);
			return 0;
		} else if (line.startsWith("delete")) {// 输入为delete时删除会议
			deleteMeeting(line);
			return 0;
		} else if (line.startsWith("query")) {// 输入为query时查询会议
			queryMeeting(line);
			return 0;
		} else {
			System.err.println("bad request！");// 对于不存在的命令给出错误提示
			return 0;
		}
	}

	/**
	 * 添加会议
	 * 
	 * @param line 命令
	 * @throws RemoteException
	 */
	public static void addMeeting(String line) throws RemoteException {
		String[] arg = line.split(" ");
		int length = arg.length;
		if (arg.length != 5) {// 参数个数不为5时，给出错误提示
			System.err.println("参数数量错误");
		} else {
			ArrayList<User> userList = userInterface.getUserList();// 获取用户列表
			User user2 = userInterface.getUserByName(arg[1]);// 创建第二个用户的实例
			if (user2 == null) {// 如果未找到该用户，给出错误提示
				System.err.println("找不到该用户");
				return;// 退出函数，继续获取输入
			} else if (user2.getName().equals(user.getName())) {// 如果输入的用户名为本人，给出错误提示
				System.err.println("错误：输入本人");
				return;// 退出函数，继续获取输入
			} else {
				meetingID++;// 会议的ID
				try {
					Date start = format.parse(arg[2]);// 会议的开始时间
					Date end = format.parse(arg[3]);// 会议的结束时间

					if (start.after(end) || start.equals(end)) {// 时间顺序错误，给出错误提示
						System.err.println("请输入正确时间顺序");
						return;// 退出函数，继续获取输入
					}
					String title = arg[4];// 会议的标题
					ArrayList<User> users = new ArrayList<User>();// 用户列表
					users.add(user);
					users.add(user2);
					meeting = new Meet(meetingID, start, end, title, users);// 实例化会议
					if (!meetingInterface.addMeeting(meeting)) {// 调用远程方法
						System.err.println("会议时间冲突");// 远程方法会进行会议时间冲突检测
					} else {
						System.out.println("添加成功");
					}
				} catch (ParseException e) {// 时间格式错误
					System.err.println("请输入正确的时间格式");
					return;// 退出函数，继续获取输入
				}
			}
		}
	}

	/**
	 * 删除会议
	 * 
	 * @param line 命令
	 * @throws RemoteException
	 */
	public static void deleteMeeting(String line) throws RemoteException {

		String[] arg = line.split(" ");
		if (arg.length != 2) {// 参数个数不为2时，给出错误提示
			System.err.println("参数个数错误");
		} else {
			int id = Integer.parseInt(arg[1]);// 会议的ID
			boolean success = meetingInterface.deleteById(id, user);// 调用远程方法
			if (success) {
				System.out.println("删除会议成功");
			} else {
				System.err.println("删除失败(ID错误 或 非创建者删除)");
			}
		}
	}

	public static void queryMeeting(String line) throws RemoteException {
		String[] arg = line.split(" ");
		ArrayList<Meet> meetingList;
		if (arg.length != 3) {// 参数个数不为3时，给出错误提示
			System.err.println("参数个数错误");
			return;
		} else {
			try {
				Date start = format.parse(arg[1]);// 会议开始时间
				Date end = format.parse(arg[2]);// 会议结束时间
				if (start.after(end) || start.equals(end)) {// 时间顺序错误
					System.err.println("请输入正确时间顺序");
					return;
				}
				meetingList = meetingInterface.queryByTime(start, end);// 调用远程方法
				if (meetingList == null) {
					System.out.println("查询为空");
				} else {// 按时间顺序，打印会议信息
					for (Meet i : meetingList) {
						System.out.println(i.toString());
					}
				}
			} catch (ParseException e) {// 时间格式错误，给出错误提示
				System.err.println("请输入正确时间格式");
				return;
			}
		}
	}

	/**
	 * 打印菜单
	 */
	public static void printIntroduction() {
		System.out.println("RMI Menu:");
		System.out.println("    1.add");
		System.out.println("        arguments: <username> <start> <end> <title>例如：2020年1月1日16:00表示为2020-01-01-16:00");
		System.out.println("    2.delete");
		System.out.println("        arguments: meetingid");
		System.out.println("    3.clear");
		System.out.println("        arguments: no args");
		System.out.println("    4.query");
		System.out.println("        arguments: <start> <end>");
		System.out.println("    5.help");
		System.out.println("        arguments: no args");
		System.out.println("    6.quit");
		System.out.println("        arguments: no args");
	}
}
