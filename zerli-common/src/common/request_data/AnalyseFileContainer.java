package common.request_data;

/**
 * Gets access to init Array function which connect to AnalyseFile class
 *
 * @author Jessica, Yarden
 */
public class AnalyseFileContainer {
	public AnalyseFile analyseFile;

	public AnalyseFileContainer() {
		analyseFile = new AnalyseFile();
	}

	public void initArray(int size) {
		analyseFile.byteArray = new byte[size];
	}

}