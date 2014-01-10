import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class ICSparser {

	ICSparser(){

	}

	public ArrayList<PreEvent> parse(String[] fileList, Color[] colorList){

		ArrayList<PreEvent> preList = new ArrayList<>();
		for(int k = 0; k < fileList.length; k++){

			String[]  icsString  = ArraysX.getCol(ArraysX.WebdlmRead(fileList[k], "@@", "utf-8"), 0);
			System.out.println(icsString.length);

			for(int i = 0; i < icsString.length; i++){
				if(icsString[i].startsWith("BEGIN:VEVENT")){
					String s = null;
					String e = null;
					String n = null;
					String l = null;
					String r = null;

					for(int j = i; j < icsString.length; j++){
						if(icsString[j].startsWith("DTSTART"))
							s = icsString[j];
						else if(icsString[j].startsWith("DTEND"))
							e = icsString[j];
						else if(icsString[j].startsWith("SUMMARY"))
							n = icsString[j];
						else if(icsString[j].startsWith("LOCATION"))
							l = icsString[j];
						else if(icsString[j].startsWith("RRULE:"))
							r = icsString[j];
						else if(icsString[j].startsWith("END:VEVENT")){
							i = j;
							break;
						}
					}

					if(s != null && e != null && n != null){
						preList.add(new PreEvent(s,e,n,l,r, colorList[k]));
					}
				}

				System.out.println();
			}
		}

		for(int i = 0; i < preList.size(); i++){
			if(preList.get(i).repeat.size() > 0){
				for(int j = 0; j < preList.get(i).repeat.size(); j++)
					preList.add(preList.get(i).repeat.get(j));
			}			
		}

		sort(preList);
		return preList;
	}

	public void sort(ArrayList<PreEvent> list){
		Collections.sort(list, new EventComapartor());
	}


	class EventComapartor implements Comparator<PreEvent>{

		@Override
		public int compare(PreEvent o1, PreEvent o2) {
			if(o1.st.before(o2.st))
				return -1;
			if(o1.st.after(o2.st))
				return 1;
			else
				return 0;
		}

	}


	class PreEvent{
		String name, location; 
		Calendar st, ed;
		String[] rules;
		ArrayList<PreEvent> repeat = new ArrayList<>();
		
		Color color;

		public PreEvent(String s, String e, String n, String l, String r, Color c){

			color = c;
			
			st = parseString(s.split(":")[1], s.indexOf("Asia/Tokyo") == -1);
			ed = parseString(e.split(":")[1], s.indexOf("Asia/Tokyo") == -1);

			name = n.split(":")[1];

			String[] lArr = l.split(":");
			if(lArr.length == 2)
				location = lArr[1];
			else
				location = "";

			if(r != null){
				r = r.substring(6, r.length());
				rules = r.split(";");

				String freq = null;
				String[] dofw = null;
				String till = null;
				boolean tzFlag = false;
				int count = -1;

				for(int i = 0; i < rules.length; i++){
					switch(rules[i].split("=")[0]){
					case "FREQ":
						freq = rules[i].split("=")[1];
						break;
					case "BYDAY":
						dofw = rules[i].split("=")[1].split(",");
						break;
					case "UNTIL":
						till = rules[i].split("=")[1];
						tzFlag = (rules[i].indexOf("Asia/Tokyo") == -1);
						break;
					case "COUNT":
						count = Integer.valueOf(rules[i].split("=")[1]);
						break;
					}
				}

				Calendar tillC = null;

				if(till != null)
					tillC = parseString(till, tzFlag);
				else
					tillC = Main.ed;

				int[] dofwi = new int[dofw.length];

				for(int i = 0; i < dofw.length; i++){
					switch(dofw[i]){
					case "SU": dofwi[i] = 1; break;
					case "MO": dofwi[i] = 2; break;
					case "TU": dofwi[i] = 3; break;
					case "WE": dofwi[i] = 4; break;
					case "TH": dofwi[i] = 5; break;
					case "FR": dofwi[i] = 6; break;
					case "SA": dofwi[i] = 7; break;
					default :
						System.out.println("NOT DEFINED " + rules[1]);
						System.exit(-1);
					}
				}

				if(freq != null){
					switch(freq){
					case "WEEKLY":
						Arrays.sort(dofwi);
						if(count == -1){
							for(Calendar counter = (Calendar) st.clone();  counter.before(tillC); counter.add(Calendar.DATE, 1)){
								if(Arrays.binarySearch(dofwi, counter.get(Calendar.DAY_OF_WEEK)) >= 0){
									Calendar cloneSt = (Calendar) counter.clone();
									int duration = (int)(ed.getTimeInMillis() - st.getTimeInMillis())/1000;
									Calendar cloneEd = (Calendar) counter.clone();
									cloneEd.add(Calendar.SECOND, duration);
									repeat.add(new PreEvent(cloneSt, cloneEd, name, location, color));
								}
							}
						} else if(count != -1){
							Calendar counter = (Calendar) st.clone();
							for(int i = 1; i <= count;){
								if(Arrays.binarySearch(dofwi, counter.get(Calendar.DAY_OF_WEEK)) >= 0){
									Calendar cloneSt = (Calendar) counter.clone();
									int duration = (int)(ed.getTimeInMillis() - st.getTimeInMillis())/1000;
									Calendar cloneEd = (Calendar) counter.clone();
									cloneEd.add(Calendar.SECOND, duration);
									repeat.add(new PreEvent(cloneSt, cloneEd, name, location, color));
									i++;
								}
								counter.add(Calendar.DATE, 1);
							}
						}
						break;
					}
				}
			}

		}

		public PreEvent(Calendar s, Calendar e, String n, String l, Color c){

			st = s;
			ed = e;

			name = n;
			location = l;

			color = c;
		}

		private int[] dateVec(String date, String time){
			//tzFlag = true -> no need to +9 GMT;

			int year = 0, month = 0, day = 0, hour = 0, minute = 0;

			year = Integer.valueOf(date.substring(0, 4));
			month = Integer.valueOf(date.substring(4, 6)) - 1;
			day = Integer.valueOf(date.substring(6, 8));
			hour = Integer.valueOf(time.substring(0, 2));
			minute = Integer.valueOf(time.substring(2, 4));


			int[] ans = {year, month, day, hour, minute};
			return ans;
		} 


		private int[] dateVec(String date){

			int year = 0, month = 0, day = 0;

			year = Integer.valueOf(date.substring(0, 4));
			month = Integer.valueOf(date.substring(4, 6)) - 1;
			day = Integer.valueOf(date.substring(6, 8));

			int[] ans = {year, month, day};
			return ans;
		}

		private Calendar parseString(String date, boolean tzFlag){

			int[] dateVec;
			Calendar cal = Calendar.getInstance();

			String[] TimeArr = date.split("T");

			if(TimeArr.length == 1){
				dateVec = dateVec(TimeArr[0]);
				cal.set(dateVec[0], dateVec[1], dateVec[2]);
			}else{
				dateVec = dateVec(TimeArr[0], TimeArr[1]);
				cal.set(dateVec[0], dateVec[1], dateVec[2], dateVec[3], dateVec[4]);
			}

			if(tzFlag)
				cal.roll(Calendar.HOUR_OF_DAY, 9);

			return cal;
		}
	}	
}

