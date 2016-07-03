package egbert;

import java.io.IOException;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.MainUI;
import view.StartupUI;

public class Egbert {

	private static final Logger log = LoggerFactory.getLogger(Egbert.class);
	public static void main(String[] args) {
		
		if (args.length > 0) {
			String IP;
			if (args.length == 2) {
				
			}
			IP = args[0];
			try {
				if (InetAddress.getByName(IP).isReachable(5000)) {
					new MainUI();
				} else {
					//System.out.println("Could not reach controller from parameter specified, going to main screen.");
					log.error("Could not reach controller from parameter specified, going to main screen.");
					try {
						new StartupUI();
					} catch (Exception e) {
						//e.printStackTrace();
						log.error("Start StartupUI Failed: {}", e.getMessage());
						System.exit(1);
					}
				}
			} catch (IOException e) {
				log.error(e.getMessage());
				System.exit(1);
			}
		}
		else {
			try {
				//new StartupUI();
				new MainUI();
			} catch (Exception e) {
				log.error("Start StartupUI Failed: {}", e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
