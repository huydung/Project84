package jobs;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringEscapeUtils;

import com.huydung.utils.MiscUtil;

import net.sf.cglib.core.Local;

import play.Logger;
import play.db.jpa.JPA;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;
import models.*;
import models.enums.ApprovalStatus;
import models.enums.DoneStatus;
import models.templates.ListTemplate;
import models.templates.ProjectListTemplate;
import models.templates.ProjectTemplate;
import models.templates.Template;

@OnApplicationStart
public class Bootstrap extends Job {
	public void doJob(){
		TimeZone.setDefault(TimeZone.getTimeZone("GTM+7:00"));
		Locale.setDefault(new Locale("vi"));
		MiscUtil.ConsoleLog("Bootstrap being called");
		if( User.count() <= 0 ){
			createUserAndProjectData();
			MiscUtil.ConsoleLog("Created data in Bootstrap");
		}
		
	}	
	
	public static void deleteAllData(){
		Fixtures.deleteDatabase();
	}
	
	public static void createUserAndProjectData(){
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
						
		//ListTemplate for Software Development
		ListTemplate blanks = new ListTemplate("Blank List", true, null);
		blanks.hasPermissions = true;
		blanks.iconPath = "/public/appicons/text.png";
		blanks.mainField = "name";
		blanks.subField = "created";		
		blanks.fields = "name:Name";		
		blanks.save();	
		
		ListTemplate milestones = new ListTemplate("MileStones", true, null);
		milestones.hasPermissions = true;
		milestones.iconPath = "/public/appicons/flag.png";
		milestones.mainField = "name";
		milestones.subField = "date";
		milestones.sort = "date ASC";
		milestones.fields = "name:Name,date:Date,checkbox:Completed?";
		milestones.save();
		
		Item it1 = Item.createFromSmartInput("Chốt Sketch và Design, thanh toán 40%", null);
		it1.save();it1.creator = null;
		milestones.addItem(it1);
		
		Item it2 = Item.createFromSmartInput("Hoàn thiện và chạy phần Website", null);
		it2.save();
		milestones.addItem(it2);
		
		Item it3 = Item.createFromSmartInput("Hoàn thiện phần back-end & Quản lý", null);
		it3.save();
		milestones.addItem(it3);
		
		Item it4 = Item.createFromSmartInput("Hướng dẫn sử dụng & Tài liệu. Thanh toán 100%.", null);
		it4.save();
		milestones.addItem(it4);
		
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
		
		
		
		ProjectTemplate blank = new ProjectTemplate(
				"Blank Project", true, null, true);
		blank.save();
		
		ProjectTemplate software = new ProjectTemplate(
				"Software Development", true, null, true);
		software.save();
		software.addListTemplate(milestones, "MileStones");
		software.addListTemplate(files, "Designs");
		software.addListTemplate(discussions, "Docs & Messages");
		software.addListTemplate(tasks, "Todos");	
		software.addListTemplate(costs, "Costs");
		software.refresh();
		
		ProjectTemplate wedding = new ProjectTemplate(
				"Wedding", true, null, true);
		wedding.save();
		
		ListTemplate guests = new ListTemplate("Guests", true, null);
		guests.hasPermissions = true;
		guests.iconPath = "/public/appicons/address-book-2.png";
		guests.mainField = "name";
		guests.subField = "phone1";		
		guests.fields = "checkbox:Sent Invitation?,name:Name,category:Category,user:Guest of,phone1:Mobile,email1:Email,number:Age,address:Address";		
		guests.save();
		
		ListTemplate tasks2 = new ListTemplate("tasks2", true, null);
		tasks2.hasPermissions = true;
		tasks2.iconPath = "/public/appicons/note-2.png";
		tasks2.mainField = "name";
		tasks2.subField = "date";		
		tasks2.fields = "name:Name,number:Priority,date:Due Date,user:Assigned To,checkbox:Completed?,category:Todo List";		
		tasks2.save();		
		
		ListTemplate discussions2 = new ListTemplate("discussions2", true, null);
		discussions2.hasPermissions = true;
		discussions2.iconPath = "/public/appicons/__discussions2.png";
		discussions2.mainField = "name";
		discussions2.subField = "user";		
		discussions2.fields = "name:Title,body:Content,user:Author,category:Category";		
		discussions2.save();
		
		ListTemplate costs2 = new ListTemplate("Shopping List", true, null);
		costs2.hasPermissions = true;
		costs2.iconPath = "/public/appicons/shop.png";
		costs2.mainField = "name";
		costs2.subField = "cost";		
		costs2.fields = "checkbox:Bought?,category:Category,name:Item,cost:Price,user:Buyer,url:Reference,date:Plan to Buy on";		
		costs2.save();
		
		wedding.addListTemplate(tasks2, "To-Do");
		wedding.addListTemplate(costs2, "To-Buy");		
		wedding.addListTemplate(guests, "Guests");
		wedding.addListTemplate(discussions2, "Discussions");
		
		wedding.refresh();
		
		//Create HD Wedding Project		
		Project wd = new Project();
		wd.name = "HD Wedding";
		cal.set(2011, 2, 10, 20, 22);
		wd.created = cal.getTime();
		wd.creator = huydung;
		wd.needMembers = true;
		cal.set(2012, 11, 8);
		wd.deadline = cal.getTime();
		wd.status = DoneStatus.ONGOING;
		wd.description = "Đám cưới mong chờ giữa Chú rể và Cô dâu";
		wd.updated = wd.created;
		wd.createAndGetResult(huydung);		
		wd.copyFromTemplate(wedding);		
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
		item1.user = hanguyen;		
		item1.number = 5;
		item1.checkbox = false;		item1.category = "Ảnh cưới";
		cal.set(2011, 5, 14);		item1.date = cal.getTime();
		item1.creator = huydung;
		item1.save();
		
		Item item2 = new Item(taskList);
		item2.name = "Tìm hiểu và chốt địa điểm chụp ảnh cưới";
		item2.user = huydung;		
		item2.number = 4;
		item2.checkbox = false;		item2.category = "Ảnh cưới";
		cal.set(2011, 5, 14);		item2.date = cal.getTime();
		item2.creator = huydung;
		item2.save();
		
		Comment m1 = new Comment();
		m1.parent = item2;
		m1.creator = huydung;
		m1.name = "Comment of huydung ";
		m1.deleted = false;
		m1.body = "Anh đề nghị đi chụp ở Asean Resort cho nó có nhiều kỉ niệm ý.";
		m1.save();
		
		Comment m2 = new Comment();
		m2.parent = item2;
		m2.creator = hanguyen;
		m2.name = "Comment of hanguyen ";
		m2.deleted = false;
		m2.body = "Em đề xuất đi Mộc Châu, thấy bạn bè em đứa nào lên đó chụp ảnh nhân dịp gì cũng đều rất đẹp ý.";
		m2.save();
		
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
		item11.creator = huydung;	
		item11.checkbox = false;		
		item11.category = "Bạn bố mẹ";
		item11.number = 54;
		item11.phone1 = "0936957034";
		item11.email1 = "lien@huydung.com";
		item11.address = "18 Yết Kiêu, Hà Nội";
		item11.save();
		
		Listing discussList = Listing.findByProjectAndName(wd, "Discussions");
		//discussions.fields = "name:Title,body:Content,user:Author,category:Category";
		Item item12 = new Item(discussList);
		item12.name = "10 đúc kết từ đám cưới xưa và nay";
		item12.creator = hanguyen;
		item12.category = "Tham khảo";
		item12.user = hanguyen;
		item12.body = "<div id=\"entry\"><div align=\"justify\">Bài viết nhằm đúc kết một cách thiết thực nhất những thay đổi trong đám cưới xưa và nay. Những lập luận tổng thể được chắt lọc sẽ giúp cho chúng ta có được một cái nhìn chung nhất về đời sống hôn nhân của xã hội hiện đại. Nhiều khi chúng ta cũng cần phải cập nhật để \"hội nhập\". Những ai cho rằng quan niệm của mình về hôn nhân là đúng đắn có thể sẽ bị bất ngờ với \"thực tế\". Và hiện trạng thực tế của hôn nhân ngày nay là gì? <i><br></i><br><div align=\"center\"><br><img title=\"Photo by daoducquan\" alt=\"đám cưới, xưa và nay, tình yêu, hôn nhân, kết hôn, vợ chồng, quan niệm, truyền thống, hiện đại, tân hôn, môn đăng hộ đối, gia đình\" src=\"http://www.marry.vn/sites/default/files/fancy/daoducquan_1.jpg\"><br>Photo by <i><a href=\"http://www.flickr.com/photos/daoducquan/\">daoducquan</a></i><br><br><br></div><i>Có thể có rất nhiều quan điểm về tuyền thống và hiện đại, nhưng chúng ta khó có thể phủ nhận những điểm được liệt kê dưới đây: </i><br><br><br><b>10 đúc kết từ đám cưới xưa và nay:<br></b><br>1. Đám cưới ngày nay không thể thiếu tình yêu. Và người ta <b>thật sự muốn sống bên nhau dài lâu.</b><br><br>2. Đám cưới<b> </b>ngày<b> </b>nay <a href=\"http://www.marry.vn/bai-viet/dam-cuoi-ngay-nay-thuong-mai-hoa-qua\">\"thương mai\"</a><b> </b>hơn, nhưng ít mang nặng tính truyền thống và phong tục hơn. <br><br>3. Đám cưới ngày nay là một \"công nghệ\" đãi tiệc. CD-CR phải trang bị cho mình những kiến thức nhất định về tổ chức tiệc cưới. <br><br>4. Những dịch vụ cưới ngày càng chuyên nghiệp và phong phú hơn, hỗ trợ cho CD-CR nhiều hơn. <br><br>5. Đêm <a href=\"http://www.marry.vn/bai-viet/dem-tan-hon-thoi-hien-dai\">tân hôn</a> của đôi vợ chồng có thể \"không phải là đêm đầu tiên\". <br><br>6. Người ta kết hôn sau khi đã sống thử. <br><br>7. Người ta cân nhắc nhiều hơn, kén chọn nhiều hơn và lấy nhau với những tư tưởng <a href=\"http://www.marry.vn/bai-viet/dinh-nghia-mon-dang-ho-doi-thoi-hien-dai\">môn đăng hộ đối</a> của thời hiện đại. <br><br>8. CD-CR lấy nhau nhưng vẫn muốn tiếp tục sự nghiệp và hoài bão của mình<br><br>9. Hạnh phúc chỉ có thể trọn vẹn khi con người ta thấy cuộc sống mình đầy đủ và ấm no<b>.</b><br><br>10. Quan niệm về cưới ngày nay hoàn toàn khác trước. <b><i>Kết hôn là một con thuyền, mà ngày nay người ta luôn muốn và cố gắng để lèo lái nó theo ý mình. </i></b><br><br><br><div align=\"center\"><img title=\"\" alt=\"\" src=\"http://www.marry.vn/sites/default/files/fancy/em-co-dong-y-lay-anh-khong.jpg\"><br></div><br><br><b>Và 10 đúc kết vui: </b><br><br>1. Đám cưới ngày xưa chú rể không nhức đầu tìm cách cầu hôn cô dâu. <br><br>2. Đám cưới ngày xưa người ta không bị \"tra tấn\" bởi \"nhạc\". <br><br>3. Chú rể mong chờ \"đem tân hôn\" hơn cả lễ cưới. <br><br>4. Cô dâu chỉ trông chờ vào bộ váy cưới. <br><br>5. CD-CR cười đơ hết cả hàm trong ngày cưới. <br><br>6. Tất cả mọi người đều muốn mình là tâm điểm của bữa tiệc. <br><br>7. CD-CR sợ không có hình đẹp vào ngày cưới. <br><br>8. Người ta không cãi nhau vì chi phí, thời gian, con người... mà họ có thể cãi nhau chỉ vì màu của một chiếc khăn ăn hay hoa trang trí. <br><br>9. Chẳng ai nhớ nổi mình đã mời những ai. <br><br>10. Ngày nay, vợ nói chồng phải \"nghe\". <br><br><br><div align=\"center\"><img title=\"\" alt=\"\" src=\"http://www.marry.vn/sites/default/files/fancy/laychong_0.jpg\"><br></div><br><br>Với tất cả những gì được nêu ở trên, đám cưới có thể xem là một cột mốc quan trọng, đặc biệt và thú vị nhất trong một đời người. Đừng quên rằng dù đời sống hiện đại có thay đổi quan niệm người ta như thế nào, thì kết hôn vẫn luôn giữ được vẻ đẹp truyền thống và ý nghĩa gắn kết thiêng liêng của nó. <br><br>Chúc bạn có một ngày cuối tuần thật thư giãn!<br><br><div align=\"right\"><i><b>Marry.vn<br>Hình ảnh: sưu tầm<br></b></i></div><br></div></div>";
		item12.save();		
		
		Item item13 = new Item(discussList);
		item13.name = "Trình tự một đám cưới ở Việt Nam";
		item13.creator = hanguyen;
		item13.category = "Tham khảo";
		item13.user = hanguyen;
		item13.body = "<div id=\"entry\"><p>Đám cưới ngày nay mà Người xưa gọi là Hôn-Lễ, theo lễ tục xưa người  ta làm lễ cưới vào buổi chiều  tối. Buổi chiều tối là lúc Dương qua Âm  lại, âm dương giao hoán với nhau  được thuần, cho nên dùng giờ này để  làm Hôn-Lễ, tức là thuận theo lẽ  tuần hoàn của trời đất.Theo xưa thì có 6 lễ, phân ra như sau :</p><h2>1.- Lễ Nạp Thái</h2>Theo  tục lệ Trung Hoa thì sau khi nghị hôn rồi, nhà trai mang sang  nhà gái  một cặp “nhạn”. Sở dĩ đem chim nhạn là vì chim nhạn là loài  chim rất  chung tình, không sánh đôi hai lần. Tương truyền rằng loài  chim nhạn rất  thảo ăn, khi chúng nó gặp mồi thì kêu nhau ăn chung, vừa  lúc đẻ trứng  thì khi nở thế nào cũng có một con trống và con mái mà  thôi. Khác với  các loại chim khác, chim nhạn khi có một con chết thì  một con còn lại  cũng buồn rầu mà chết theo. Sau này, người Trung Hoa  nào còn theo cổ lễ  thì chỉ dùng ngỗng thay thế cho chim nhạn. (Loài  ngỗng tuy ngông nghênh,  nhưng rất chung tình).<h2>2.- Lễ Vấn Danh</h2>Là hỏi tên và họ của cô gái là gì, được bao nhiêu tuổi, đã có hứa hôn với ai chưa.<br><a href=\"http://cdn.vnwordpress.com/cuoi/wp-content/uploads/2011/06/Cuoi-le-van-danh.jpg\"><img title=\"Cuoi-le-van-danh\" src=\"http://cdn.vnwordpress.com/cuoi/wp-content/uploads/2011/06/Cuoi-le-van-danh.jpg\" alt=\"Cuoi le van danh Trình Tự Một Đám Cưới ở Việt Nam   cưới 360\" height=\"328\" width=\"448\"></a><h2><br></h2><h2>3.- Lễ Nạp Cát</h2>Là sắm sửa lễ phẩm đem sang nhà gái cầu hôn. Tùy theo nhà giàu thì lễ quí, còn nghèo thì chút đỉnh gọi là.<br><a href=\"http://cdn.vnwordpress.com/cuoi/wp-content/uploads/2011/06/Cuoi-le-vat.jpg\"><img title=\"Cuoi-le-vat\" src=\"http://cdn.vnwordpress.com/cuoi/wp-content/uploads/2011/06/Cuoi-le-vat.jpg\" alt=\"Cuoi le vat Trình Tự Một Đám Cưới ở Việt Nam   cưới 360\" height=\"300\" width=\"500\"></a><h2><br></h2><h2>4.- Lễ Nạp Chưng</h2>Lễ  Nạp Chưng hay là Lễ Nạp Tệ (“chưng” nghĩa là chứng, “Tệ” nghĩa là  lụa)  là lễ đem hàng lụa hay vật phẩm quí giá đến nhà gái làm tang  chứng cho  sự hứa hôn chắc chắn, rồi chỉ chờ ngày cưới dâu.<br><a href=\"http://cdn.vnwordpress.com/cuoi/wp-content/uploads/2011/06/Cuoi-le-nap-tai.jpg\"><img title=\"Cuoi-le-nap-tai\" src=\"http://cdn.vnwordpress.com/cuoi/wp-content/uploads/2011/06/Cuoi-le-nap-tai.jpg\" alt=\"Cuoi le nap tai Trình Tự Một Đám Cưới ở Việt Nam   cưới 360\" height=\"405\" width=\"540\"></a><h2><br></h2><h2>5.- Lễ Thỉnh Kỳ</h2>Là  Lễ xin định ngày giờ làm Lễ Cưới, nhưng ngày giờ cũng do bên trai  định,  rồi hỏi lại ý kiến bên gái mà thôi, song thế nào nhà gái cũng  tùy ý bên  trai.<h2>6.- Lễ Thân Nghinh</h2>Là đã được nhà gái ưng thuận, ngày giờ đã định của bên trai.Bên nhà trai đem lễ vật sang làm lễ rước dâu về.<br><a href=\"http://cdn.vnwordpress.com/cuoi/wp-content/uploads/2011/06/Cuoi-le-don-dau.jpg\"><img title=\"Cuoi-le-don-dau\" src=\"http://cdn.vnwordpress.com/cuoi/wp-content/uploads/2011/06/Cuoi-le-don-dau.jpg\" alt=\"Cuoi le don dau Trình Tự Một Đám Cưới ở Việt Nam   cưới 360\" height=\"328\" width=\"500\"></a><br><p></p></div>";		
		item13.save();
		
		//Create sample Items for Website Project Template 
		
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
