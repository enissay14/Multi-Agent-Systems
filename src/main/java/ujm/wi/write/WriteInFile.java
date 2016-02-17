package ujm.wi.write;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import ujm.wi.entities.Agent;
import ujm.wi.enums.JCMFile;
import ujm.wi.enums.Paths;

public class WriteInFile {
	
	public static void main(String[] args) {
		//writeFile();
	}
	
	
	
	public static boolean writeFile(List<Agent> agents){
		
		try {
			int random = (int )(Math.random() * agents.size() + 1);

			/* fisrt line in th jcm file */
			String line = JCMFile.MAS + " " + JCMFile.PROJECT_NAME + " {\n";

			/* open file */
			File file = new File(Paths.JCM.toString());

			/* if file doesn't exists, then create it */ 
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(line);
			
			for (Agent agent : agents) {
				
				if(Integer.parseInt(agent.getName()) == random){
					line = "	"+JCMFile.AGENT+" ag"+agent.getName()+": "+JCMFile.ASL_FILE_NAME_ROOT + " {\n";
				}else{
					line = "	"+JCMFile.AGENT+" ag"+agent.getName()+": "+JCMFile.ASL_FILE_NAME + " {\n";
				}
				 
				bw.write(line);
				line = "		"+JCMFile.BELIEFS+": "+ JCMFile.NEIGHBORS+"([";
				//System.out.print(s + " : [");
				for (String neighbor : agent.getNeighbors()) {
					line += "ag"+neighbor+",";
					System.out.print(neighbor + " ");
				}
				line = line.substring(0, line.lastIndexOf(","))+"])\n	}\n";
				bw.write(line);
				
				//System.out.print("]\n");
			}
			
			
			line = "\n	asl-path: src/agt\n"
				 + "			  src/agt/inc\n\n}";
			bw.write(line);
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} 
		
		return true;
	}
}
