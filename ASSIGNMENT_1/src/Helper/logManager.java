package Helper;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class logManager {

	private Logger logger = null;
	String logFileName = "";

	public logManager(String logFileName) {

		synchronized(this)
		{
			logger = Logger.getLogger(logFileName);
			this.logFileName = logFileName;	
		}
		

	}

	public logManager() {

	}

	class plainFormatter extends Formatter {

		@Override
		public String format(LogRecord record) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(record.getMessage());
			return stringBuilder.toString();
		}

	}

	public synchronized void insertMessage(String Message) {

		FileHandler fileHandler = null;
		try {
			fileHandler = new FileHandler(logFileName + ".log", true);
		} catch (SecurityException | IOException e) {

			e.printStackTrace();
		}
		logger.addHandler(fileHandler);
		fileHandler.setFormatter(new plainFormatter());
		logger.setUseParentHandlers(false);
		logger.info(Message);
		fileHandler.close();
	}

	public synchronized void insertMessageWithFile(String filePath, String Message) {

		String root = "";
		root = System.getProperty("user.dir");
		root = root + "\\PlayerLogs\\" + filePath;
		this.logFileName = root;
		logger = Logger.getLogger(root);
		FileHandler fileHandler = null;
		try {
			fileHandler = new FileHandler(logFileName + ".log", true);
		} catch (SecurityException | IOException e) {

			e.printStackTrace();
		}
		logger.addHandler(fileHandler);
		fileHandler.setFormatter(new plainFormatter());
		logger.setUseParentHandlers(false);
		logger.info(Message);
		fileHandler.close();
	}

}
