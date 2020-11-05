package bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/**
 * MEET CLASS
 * @author ljl
 *
 */
public class Meet implements Serializable{

	private int meetingId;//会议的ID
	private Date startTime;//开始时间
	private Date endTime;//结束时间
	private String title;//标题
	private ArrayList<User> participants=new ArrayList<User>();//参与者
	/**
	 * 无参构造函数
	 */
	public Meet(){
	}
	/**
	 * 有参构造函数
	 * @param meetingId 会议的ID
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param title 会议的标题
	 * @param participants 参与者
	 */
	public Meet(int meetingId, Date startTime, Date endTime, String title,
			ArrayList<User> participants) {
		super();
		this.meetingId = meetingId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.title = title;
		this.participants = participants;
	}


	public int getMeetingId() {
		return meetingId;
	}


	public void setMeetingId(int meetingId) {
		this.meetingId = meetingId;
	}


	public Date getStartTime() {
		return startTime;
	}


	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}


	public Date getEndTime() {
		return endTime;
	}


	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public ArrayList<User> getParticipants() {
		return participants;
	}


	public void setParticipants(ArrayList<User> participants) {
		this.participants = participants;
	}


	@Override
	public String toString() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startTimeDateString = formatter.format(startTime);
		String endTimeDateString = formatter.format(endTime);
		return "Meeting"+meetingId+" [startTime=" + startTimeDateString
				+ ", endTime=" + endTimeDateString + ", title=" + title
				+ ", participants=" + participants +"]";
	}

	
	
}
