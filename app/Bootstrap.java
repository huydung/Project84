import java.math.BigDecimal;
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
		
		User hanguyen = new User();
		hanguyen.fullName = "Hà Thúy Nguyễn";
		hanguyen.email = "oakman.hd@gmail.com";
		hanguyen.hasProfile = true;
		hanguyen.dateFormat = "dd.MM.yyyy";
		hanguyen.timeZone = "Asia/Ho_Chi_Minh";
		hanguyen.identifier = "https://www.google.com/profiles/110870856863940855723";
		cal.set(2011, 4, 12, 19, 30);
		hanguyen.lastLoggedIn = cal.getTime();
		hanguyen.mobile = "0985898137";
		hanguyen.nickName = "thuyha";
		hanguyen.save();
		
		User kimloan = new User();
		kimloan.fullName = "Loan Hoàng Kim";
		kimloan.email = "vannessars@yahoo.com";
		kimloan.hasProfile = true;
		kimloan.dateFormat = "dd.MM.yyyy";
		kimloan.timeZone = "Asia/Ho_Chi_Minh";
		kimloan.identifier = "https://me.yahoo.com/a/WJCrkRV7qoZX36B_BJVo2CX4UqWGxgQ-#53ea2";
		cal.set(2011, 4, 24, 19, 30);
		kimloan.lastLoggedIn = cal.getTime();
		kimloan.mobile = "0985898137";
		kimloan.nickName = "kimloan";
		kimloan.save();
		
		User minhduc = new User();
		minhduc.fullName = "Đức Mạnh Trần";
		minhduc.email = "contact@huydung.com";
		minhduc.hasProfile = true;
		minhduc.dateFormat = "dd/MM/yyyy";
		minhduc.timeZone = "Asia/Ho_Chi_Minh";
		minhduc.identifier = "https://www.google.com/profiles/102417840131513894590";
		cal.set(2011, 4, 24, 19, 30);
		minhduc.lastLoggedIn = cal.getTime();
		minhduc.mobile = "0985898137";
		minhduc.nickName = "minhduc";
		minhduc.save();
						
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
		
		ListTemplate costs = new ListTemplate("Shopping List", true, null);
		costs.hasPermissions = true;
		costs.iconPath = "/public/appicons/shop.png";
		costs.mainField = "name";
		costs.subField = "cost";		
		costs.fields = "checkbox:Bought?,category:Category,name:Item,cost:Price,user:Buyer,url:Reference,date:Plan to Buy on";		
		costs.save();
		
		ListTemplate guests = new ListTemplate("Guests", true, null);
		guests.hasPermissions = true;
		guests.iconPath = "/public/appicons/address-book-2.png";
		guests.mainField = "name";
		guests.subField = "phone1";		
		guests.fields = "checkbox:Sent Invitation?,name:Name,category:Category,user:Guest of,phone1:Mobile,email1:Email,number:Age,address:Address";		
		guests.save();
		
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
		wedding.addList(tasks, "To-Do");
		wedding.addList(costs, "To-Buy");		
		wedding.addList(guests, "Guests");
		wedding.addList(files, "Files");
		
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
		Listing taskList = Listing.findByProjectAndName(wd, "To-Do");
		
		Item item1 = new Item(taskList);
		item1.name = "Tìm hiểu và lựa chọn studio chụp ảnh cưới";
		item1.user = hanguyen;		item1.number = 5;
		item1.checkbox = false;		item1.category = "Ảnh cưới";
		cal.set(2011, 5, 14);		item1.date = cal.getTime();
		item1.creator = huydung;
		item1.save();
		
		Item item2 = new Item(taskList);
		item2.name = "Tìm hiểu và chốt địa điểm chụp ảnh cưới";
		item2.user = huydung;		item2.number = 4;
		item2.checkbox = false;		item2.category = "Ảnh cưới";
		cal.set(2011, 5, 14);		item2.date = cal.getTime();
		item2.creator = huydung;
		item2.save();
		
		Item item3 = new Item(taskList);
		item3.name = "Chọn ảnh để phóng to (3 cái) và chọn mẫu album";
		item3.user = null;		    item3.number = 3;
		item3.checkbox = false;		item3.category = "Ảnh cưới";
		cal.set(2011, 5, 20);		item3.date = cal.getTime();
		item3.creator = huydung;
		item3.save();
		
		Item item4 = new Item(taskList);
		item4.name = "Liên hệ bạn bè và chốt Danh sách bê tráp";
		item4.user = huydung;		item4.number = 4;
		item4.checkbox = false;		item4.category = "Đám hỏi";
		cal.set(2011, 5, 24);		item4.date = cal.getTime();		
		item4.creator = huydung;
		item4.save();
		
		Item item5 = new Item(taskList);
		item5.name = "Liên hệ bạn bè và chốt Danh sách đỡ tráp";
		item5.user = hanguyen;		item5.number = 4;
		item5.checkbox = true;		item5.category = "Đám hỏi";
		cal.set(2011, 5, 24);		item5.date = cal.getTime();
		item5.creator = huydung;
		item5.save();
		
		Listing costList = Listing.findByProjectAndName(wd, "To-Buy");
		
		Item item6 = new Item(costList);
		item6.name = "TV";
		item6.user = huydung;		
		item6.checkbox = false;		
		item6.category = "Đồ gia dụng";
		cal.set(2011, 5, 15);		
		item6.date = cal.getTime();
		item6.creator = huydung;
		item6.url = "http://www.sony.com.vn/product/kdl-55hx925";
		item6.cost = new BigDecimal(69900000);
		item6.cost_amount = 1;
		item6.cost_currency = "VND";
		item6.save();
		
		Item item7 = new Item(costList);
		item7.name = "Điều hòa";
		item7.user = huydung;		
		item7.checkbox = false;		
		item7.category = "Đồ gia dụng";
		cal.set(2011, 5, 17);		
		item7.date = cal.getTime();
		item7.creator = huydung;
		item7.url ="http://www.nguyenkim.com/may-lanh-panasonic-cu-cs-s10mkh-8.html";
		item7.cost = new BigDecimal(10200000);
		item7.cost_amount = 2;
		item7.cost_currency = "VND";
		item7.save();
		
		Item item8 = new Item(costList);
		item8.name = "Comple cho chú rể";
		item8.user = hanguyen;		
		item8.checkbox = false;		
		item8.category = "Phục trang";
		cal.set(2011, 5, 19);		
		item8.date = cal.getTime();
		item8.creator = huydung;		
		item8.cost = new BigDecimal(500);
		item8.cost_amount = 1;
		item8.cost_currency = "USD";
		item8.save();
		
		Listing guestList = Listing.findByProjectAndName(wd, "Guests");
		
		Item item9 = new Item(guestList);
		item9.name = "Nguyễn Hồng Nhung";
		item9.user = huydung;		
		item9.checkbox = false;		
		item9.category = "Bạn bè";
		item9.number = 24;
		item9.creator = hanguyen;
		item9.phone1 = "0987934645";
		item9.email1 = "hongnhung@huydung.com";
		item9.address = "56 Nguyễn Chí Thanh, Hà Nội";
		item9.save();
		
		Item item10 = new Item(guestList);
		item10.name = "Lưu Hồng Anh";
		item10.user = huydung;		
		item10.checkbox = true;		
		item10.category = "Bạn bè";
		item10.number = 24;
		item10.creator = hanguyen;
		item10.phone1 = "098778974";
		item10.email1 = "honganh@huydung.com";
		item10.address = "19 Nguyễn Trãi, Hà Nội";
		item10.address_lat = "21.00245";
		item10.address_lan = "105.81978";
		item10.save();
		
		Item item11 = new Item(guestList);
		item11.name = "Cô Liên Nhượng";
		item11.user = huydung;		
		item11.checkbox = false;		
		item11.category = "Bạn bố mẹ";
		item11.number = 54;
		item11.phone1 = "0936957034";
		item11.email1 = "lien@huydung.com";
		item11.address = "18 Yết Kiêu, Hà Nội";
		item11.save();
		
		
		Membership bride = Membership.findByProjectAndUser(wd, hanguyen);
		bride.status = ApprovalStatus.ACCEPTED;
		bride.save();
		wd.addMember("vannessars@yahoo.com", "Wedding Planner", false, huydung);
		Membership wp = Membership.findByProjectAndUser(wd, kimloan);
		wp.status = ApprovalStatus.ACCEPTED;
		wp.save();
		wd.addMember("contact@huydung.com", "Photographer", false, hanguyen);
		Membership pt = Membership.findByProjectAndUser(wd, minhduc);
		pt.status = ApprovalStatus.ACCEPTED;
		pt.save();
		
	}
}
