package interface_;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;

import bean.Meet;
import bean.User;

/**
 * 操作会议的接口
 * @author ljl
 *
 */
public interface MeetInterface extends Remote {
	/**
	 * 按时间查询会议
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return 按时间顺序排列的会议列表
	 * @throws RemoteException
	 */
	ArrayList<Meet> queryByTime(Date startTime, Date endTime)
			throws RemoteException;
	/**
	 * 添加会议
	 * @param meeting 会议的实例
	 * @return 是否添加成功
	 * @throws RemoteException
	 */
	boolean addMeeting(Meet meeting) throws RemoteException;
	/**
	 * 清空会议
	 * @throws RemoteException
	 */
	void clearMeeting() throws RemoteException;
	/**
	 * 按照会议和用户删除会议
	 * @param meetingID 会议的ID
	 * @param user 用户的实例
	 * @return 是否成功删除会议
	 * @throws RemoteException
	 */
	boolean deleteById(int meetingID, User user) throws RemoteException;
	/**
	 * 额外添加的方法，方便操作：获取会议列表
	 * @return 会议列表
	 * @throws RemoteException
	 */
	ArrayList<Meet> getMeetingList() throws RemoteException;

}
