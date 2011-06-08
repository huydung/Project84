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
		tasks.fields = "name:Name,number:Priority,date:Due Date,user:Assigned To,check:Completed?";		
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
		
		/*
		ListTemplate images = new ListTemplate("Images", true, null,
				"Image:[filePath,mimeType,category]", true);
		images.save();
		
		ListTemplate links = new ListTemplate("Links", true, null,
				"Link:[url,category]", true);
		links.save();
		
		
		
		ListTemplate assets = new ListTemplate("Assets", true, null, 
				"Asset:[price,amount,currency]", true);
		assets.save();
		
		ListTemplate contacts = new ListTemplate("Contacts", true, null, 
				"Contact:[email1,email2,phone1,phone2,address,lat,lan]", true);
		contacts.save();
		
		ListTemplate events = new ListTemplate("Events", true, null, 
				"Event:[date,address,lat,lan]", true);
		events.save();
		*/
		
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
		//wedding.addList(contacts, "People");
		//wedding.addList(events, "Events");
		//wedding.addList(links, "Links");
		
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
		
		Membership bride = Membership.findByProjectAndUser(wd, ngochien);
		bride.status = ApprovalStatus.ACCEPTED;
		bride.save();
		wd.addMember("vannessars@yahoo.com", "Bridesmaid", false, huydung);
		
	}
}
