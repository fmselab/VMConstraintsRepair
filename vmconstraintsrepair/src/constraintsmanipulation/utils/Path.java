package constraintsmanipulation.utils;

/**
 * To get the path of system executable independently from the operating system used.
 *
 * @author marcoradavelli
 */
public class Path {

	/**
	 * Gets the python 3.
	 *
	 * @return the path to python3 executable
	 */
	public static String getPython3() {
		if (OSValidator.isWindows()) {
			return "C:\\Windows\\py.exe";
		} else if (OSValidator.isMac()) {
			return "/usr/local/bin/python3";
		} else if (OSValidator.isUnix()) {
			return "/usr/bin/python3";
		}
		return "";
	}

	/**
	 * Gets the python.
	 *
	 * @return the path to python executable
	 */
	public static String getPython() {
		if (OSValidator.isWindows()) {
			return "C:\\Windows\\py.exe";
		} else if (OSValidator.isMac()) {
			return "/usr/local/bin/python";
		} else if (OSValidator.isUnix()) {
			return "/usr/bin/python";
		}
		return "";
	}

	/**
	 * The Class OSValidator.
	 */
	public static class OSValidator {

		/** The os. */
		private static String OS = System.getProperty("os.name").toLowerCase();

		/* public static void main(String[] args) {

	        System.out.println(OS);

	        if (isWindows()) {
	            System.out.println("This is Windows");
	        } else if (isMac()) {
	            System.out.println("This is Mac");
	        } else if (isUnix()) {
	            System.out.println("This is Unix or Linux");
	        } else if (isSolaris()) {
	            System.out.println("This is Solaris");
	        } else {
	            System.out.println("Your OS is not support!!");
	        }
	    }*/

		/**
		 * Checks if is windows.
		 *
		 * @return true, if is windows
		 */
		public static boolean isWindows() {
			return (OS.indexOf("win") >= 0);
		}

		/**
		 * Checks if is mac.
		 *
		 * @return true, if is mac
		 */
		public static boolean isMac() {
			return (OS.indexOf("mac") >= 0);
		}

		/**
		 * Checks if is unix.
		 *
		 * @return true, if is unix
		 */
		public static boolean isUnix() {
			return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
		}

		/**
		 * Checks if is solaris.
		 *
		 * @return true, if is solaris
		 */
		public static boolean isSolaris() {
			return (OS.indexOf("sunos") >= 0);
		}
		
		/**
		 * Gets the os.
		 *
		 * @return the os
		 */
		public static String getOS(){
			if (isWindows()) {
				return "win";
			} else if (isMac()) {
				return "osx";
			} else if (isUnix()) {
				return "uni";
			} else if (isSolaris()) {
				return "sol";
			} else {
				return "err";
			}
		}

	}
}
