import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import net.sf.cglib.core.Local;

import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;
import models.*;
import models.enums.ApprovalStatus;
import models.enums.DoneStatus;
import models.templates.ListTemplate;
import models.templates.ProjectListTemplate;
import models.templates.ProjectTemplate;

@OnApplicationStart
public class Bootstrap extends Job {
	public void doJob(){
		TimeZone.setDefault(TimeZone.getTimeZone("GTM+7:00"));
		Locale.setDefault(new Locale("vi"));
		
		createUserAndProjectData();
		
	}	

	
	protected void createUserAndProjectData(){
		//Create Users
		Calendar cal = new GregorianCalendar(2011, 4, 11, 22, 0);
		cal.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
		
		User huydung = new User();
		huydung.fullName = "Dũng Huy Nguyễn";
		huydung.email = "contact.huydung@gmail.com";
		huydung.hasProfile = true;
		huydung.dateFormat = "dd.MM.yyyy";
		huydung.timeZone = "Asia/Ho_Chi_Minh";
		huydung.identifier = "https://www.google.com/profiles/110714496619404851032";
		huydung.lastLoggedIn = cal.getTime();
		huydung.mobile = "0984903707";
		huydung.nickName = "huydung";
		huydung.save();
		
		User ngochien = new User();
		ngochien.fullName = "Hiền Nguyễn Ngọc";
		ngochien.email = "oakman.hd@gmail.com";
		ngochien.hasProfile = true;
		ngochien.dateFormat = "dd/MM/yyyy";
		ngochien.timeZone = "Asia/Ho_Chi_Minh";
		ngochien.identifier = "https://www.google.com/profiles/110870856863940855723";
		cal.set(2011, 4, 12, 19, 30);
		ngochien.lastLoggedIn = cal.getTime();
		ngochien.mobile = "0985898137";
		ngochien.nickName = "ngochien";
		ngochien.save();
						
		//Create Project Template and List Template
		ListTemplate blanks = new ListTemplate("Blank List", true, null);
		blanks.hasPermissions = true;
		blanks.iconPath = "/public/appicons/text.png";
		blanks.mainField = "name";
		blanks.subField = "created";		
		blanks.fields = "name:Name";		
		blanks.save();	
		
		ListTemplate tasks = new ListTemplate("Tasks", true, null);
		tasks.hasPermissions = true;
		tasks.iconPath = "/public/appicons/note-2.png";
		tasks.mainField = "name";
		tasks.subField = "date";		
		tasks.fields = "name:Name,number:Priority,date:Due Date,user:Assigned To,checkbox:Completed?,category:Todo List";		
		tasks.save();		
		
		ListTemplate files = new ListTemplate("Files", true, null);
		files.hasPermissions = true;
		files.iconPath = "/public/appicons/finder.png";
		files.mainField = "name";
		files.subField = "created";		
		files.fields = "name:Name,file:File,user:Author,category:Category";		
		files.save();
		
		ListTemplate discussions = new ListTemplate("Discussions", true, null);
		discussions.hasPermissions = true;
		discussions.iconPath = "/public/appicons/__discussions.png";
		discussions.mainField = "name";
		discussions.subField = "user";		
		discussions.fields = "name:Title,body:Content,user:Author,category:Category";		
		discussions.save();
		
		
		ProjectTemplate blank = new ProjectTemplate(
				"Blank Project", true, null, true);
		blank.save();
		
		ProjectTemplate software = new ProjectTemplate(
				"Software Development", true, null, true);
		software.save();
		software.addList(tasks, "Todos");
		software.addList(files, "Documents");
		software.addList(discussions, "Discussions");	
		software.refresh();
		
		ProjectTemplate wedding = new ProjectTemplate(
				"Wedding", true, null, true);
		wedding.save();
		wedding.addList(tasks, "Todos");
		wedding.addList(files, "Files");
		wedding.addList(discussions, "Discussions");
		wedding.refresh();
		
		//Create 2 Projects
		Project ol = new Project();
		
		ol.name = "OrangeLife";
		cal.set(2011, 4, 10, 20, 22);
		ol.created = cal.getTime();
		ol.creator = huydung;
		cal.set(2011, 4, 31);
		ol.deadline = cal.getTime();
		ol.setStatus( DoneStatus.ONGOING );
		ol.description = "Dự án xây dựng website cho doanh nghiệp nước trái cây phục vụ tận nơi OrangeLife.com.vn";
		ol.updated = ol.created;
		ol.saveAndGetResult(huydung);
		ol.copyFromTemplate(software);
		ol.buildRolePermissions();
		ol.assignCreator(huydung, "Manager");
		ol.addMember("havu.hrc@gmail.com", "Client", true, huydung);
		
		Project wd = new Project();
		wd.name = "HD Wedding";
		cal.set(2011, 2, 10, 20, 22);
		wd.created = cal.getTime();
		wd.creator = huydung;
		cal.set(2012, 11, 8);
		wd.deadline = cal.getTime();
		wd.setStatus( DoneStatus.ONGOING );
		wd.description = "Đám cưới mong chờ giữa Huy Dũng và Ngọc Hiền";
		wd.updated = ol.created;
		wd.saveAndGetResult(huydung);	
		wd.copyFromTemplate(wedding);
		wd.buildRolePermissions();
		wd.assignCreator(huydung, "Broom");
		wd.addMember("oakman.hd@gmail.com", "Bride", false, huydung);
		
		/* Add sample items */
		//tasks.fields = "
		//name:Name,
		//number:Priority,
		//date:Due Date,
		//user:Assigned To,
		//check:Completed?,
		//category:Todo List";
		Listing taskList = Listing.findByProjectAndName(wd, "Tasks");
		
		Item item1 = new Item(taskList);
		item1.name = "Tìm hiểu và lựa chọn studio chụp ảnh cưới";
		item1.user = ngochien;		item1.number = 5;
		item1.checkbox = false;		item1.category = "Ảnh cưới";
		cal.set(2011, 5, 14);		item1.date = cal.getTime();
		item1.created = new Date(); item1.creator = huydung;

		item1.save();
		
		Item item2 = new Item(taskList);
		item2.name = "Tìm hiểu và chốt địa điểm chụp ảnh cưới";
		item2.user = huydung;		item2.number = 4;
		item2.checkbox = false;		item2.category = "Ảnh cưới";
		cal.set(2011, 5, 14);		item2.date = cal.getTime();
		item2.created = new Date(); item2.creator = huydung;
		item2.save();
		
		Item item3 = new Item(taskList);
		item3.name = "Chọn ảnh để phóng to (3 cái) và chọn mẫu album";
		item3.user = null;		    item3.number = 3;
		item3.checkbox = false;		item3.category = "Ảnh cưới";
		cal.set(2011, 5, 20);		item3.date = cal.getTime();
		item3.created = new Date(); item3.creator = huydung;
		item3.save();
		
		Item item4 = new Item(taskList);
		item4.name = "Liên hệ bạn bè và chốt Danh sách bê tráp";
		item4.user = huydung;		item4.number = 4;
		item4.checkbox = false;		item4.category = "Đám hỏi";
		cal.set(2011, 5, 24);		item4.date = cal.getTime();
		item4.created = new Date(); item4.creator = huydung;
		item4.save();
		
		Item item5 = new Item(taskList);
		item5.name = "Liên hệ bạn bè và chốt Danh sách đỡ tráp";
		item5.user = ngochien;		item5.number = 4;
		item5.checkbox = true;			item5.category = "Đám hỏi";
		cal.set(2011, 5, 24);		item5.date = cal.getTime();
		item5.created = new Date(); item5.creator = huydung;
		item5.save();
		
		Membership bride = Membership.findByProjectAndUser(wd, ngochien);
		bride.status = ApprovalStatus.ACCEPTED;
		bride.save();
		wd.addMember("vannessars@yahoo.com", "Bridesmaid", false, huydung);
		
	}
}
