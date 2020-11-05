/**
 * 
 */
package server;
import interface_.MeetInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;

import bean.Meet;
import bean.User;

/**
 * MeetingInterface的实现类
 * @author ljl
 *
 */
public class MeetImplement extends UnicastRemoteObject implements
MeetInterface {
	private static final long serialVersionUID = 1L;
	private ArrayList<Meet> meetingList = new ArrayList<Meet>();// 会议列表

/**
* 无参构造函数，必须存在，且抛出RemoteException
* 
* @throws RemoteException
*/
public MeetImplement() throws RemoteException {
// System.out.println("MeetingImplement--OK");
}

@Override
public ArrayList<Meet> getMeetingList() throws RemoteException {
	return this.meetingList;//返回meetingList
}

@Override
	public ArrayList<Meet> queryByTime(Date startTime, Date endTime)
	throws RemoteException {

	if (meetingList.isEmpty())
	return null;// 当前会议列表为空，直接退出方法
	ArrayList<Meet> list = new ArrayList<Meet>();// 结果列表
/*
 * Step 1:查找所有符合条件的会议
 * 查找[startTime,endTime]闭区间内的所有会议
 */
	for (Meet meeting : meetingList) {
	if (!meeting.getStartTime().before(startTime)
			&& !meeting.getEndTime().after(endTime)) {
		list.add(meeting);
	}
}
/*
 * Step 2:判断结果列表是否为空，为空直接跳出方法
 */
	if (list.isEmpty())
		return null;
/*
 * Step 3:对结果列表中的会议，按时间顺序排序
 */
	Meet[] meetings = new Meet[list.size()];
	list.toArray(meetings);//转换为数组
	Sort(meetings);// 排序
	ArrayList<Meet> result = new ArrayList<Meet>();// 重建结果列表
	for (int i = 0; i < list.size(); i++) {
		result.add(meetings[i]);
}
	return result;// 返回结果
}

/**
* 排序
* 
* @param a
*            待排序数组
*/
private static void Sort(Meet[] a) {
	int n = a.length;
//冒泡排序
	for (int i = 0; i < n - 1; i++) {
		for (int j = 0; j < n - i - 1; j++) {
			if (a[j].getStartTime().after(a[j + 1].getStartTime())) {
				swap(a, j, j + 1);
			}
		}
	}
}

/**
* 交换数组的元素位置
* @param list 待处理数组
* @param k 位置
* @param i 位置
*/
private static void swap(Meet[] list, int k, int i) {
	Meet temp = list[k];
	list[k] = list[i];
	list[i] = temp;
}

@Override
public boolean addMeeting(Meet meeting) throws RemoteException {
	Date start = meeting.getStartTime();
	Date end = meeting.getEndTime();
/*
 * 时间重叠检测
 */
	if (!meetingList.isEmpty()) {// 如果当前会议列表为空，不必检测
		for (Meet i : meetingList) {
			if (!i.getStartTime().after(start)
					&& !start.after(i.getEndTime())) {
				//start夹在中间
				return false;// 返回结果，添加失败
			} else if (!i.getStartTime().after(end)
					&& !end.after(i.getEndTime())) {
				//end夹在中间
				return false;// 返回结果，添加失败
			} else if (!start.after(i.getStartTime())
					&& !i.getStartTime().after(end)) {
				//getStartTime夹在中间
				return false;// 返回结果，添加失败
			} else if (!start.after(i.getEndTime())
					&& !i.getEndTime().after(end)) {
				//getEndTime夹在中间
				return false;// 返回结果，添加失败
			}
		}
	}
	this.meetingList.add(meeting);// 调价会议
	return true;// 返回结果，添加成功
}

@Override
public void clearMeeting() throws RemoteException {
	this.meetingList.clear();//清空meetingList
}

@Override
public boolean deleteById(int meetingID, User user) throws RemoteException {
/*
 * 查找符合条件的会议
 */
	for (Meet i : meetingList) {
		if (i.getMeetingId() == meetingID) {// ID符合
			ArrayList<User> u = i.getParticipants();//获取参与者列表
			if(u.get(0).getName().equals(user.getName())){// 执行操作的人是创建会议的人
	//			System.out.println(u.get(0).getName());
	//			System.out.println(u.get(1).getName());
				meetingList.remove(i);// 移除会议
				return true;// 返回结果，删除成功
			}
		}
	}
	return false;// 返回结果，删除失败
	}
}
