import java.util.Locale;
import java.util.TimeZone;

import net.sf.cglib.core.Local;

import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;
import models.*;

@OnApplicationStart
public class Bootstrap extends Job {
	public void doJob(){
		TimeZone.setDefault(TimeZone.getTimeZone("GTM+7:00"));
		Locale.setDefault(new Locale("vi"));
		//if(User.count() == 0){
		//	Fixtures.load("data.yml");
		//}
	}	
}
