import java.util.ArrayList;
import java.util.Calendar;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public class Main {

	static PdfWriter pdfwriter = null;
	static BaseFont baseFont = null;
	static PdfContentByte pdfContentByte;
	static Document doc;

	static int counter = 0;

	static PdfGraphics2D pdfGraphics2D;

	static Calendar st, ed;

	public static void main(String[] args){

		ICSparser icsp = new ICSparser();
		ArrayList<String> fileListArray = ArraysX.readLines("setting.conf", "sjis");
		String[] fileList = ArraysX.toArray(fileListArray);
		
//		fileList[0] = "https://www.google.com/calendar/ical/21gl2ihvhtdebj93v0lv6jr93s%40group.calendar.google.com/private-3f6193d9f1d3d700c85ab396bd09fa5f/basic.ics";
//		fileList[1] = "https://www.google.com/calendar/ical/cushi4htofdql5h0hblka5i8og%40group.calendar.google.com/private-9e63b2303566675ef861e39f922fa16e/basic.ics";
//		fileList[2] = "https://www.google.com/calendar/ical/tsuzuki.keita%40gmail.com/private-94a8ece4ec7b1bc37b93f17e34673084/basic.ics";

		Color[] colorList = new Color[3];
		colorList[0] = new Color(255,163,176);
		colorList[1] = new Color(178,255,155);
		colorList[2] = new Color(133,162,255);

		st = Calendar.getInstance();
		st.set(Calendar.HOUR_OF_DAY, 23);
		ed = Calendar.getInstance();
		ed.set(2014, 2, 31, 0, 0, 0);

		st.add(Calendar.DAY_OF_MONTH, -st.get(Calendar.DAY_OF_WEEK) + 1);
		ed.add(Calendar.DAY_OF_MONTH, st.get(Calendar.DAY_OF_WEEK));

		ArrayList<ICSparser.PreEvent> pre = icsp.parse(fileList, colorList);

		for(int i = 0; i < pre.size(); i++){
			System.out.println(pre.get(i).name + ":" + printCalnedar(pre.get(i).st) + "--" + printCalnedar(pre.get(i).ed));
		}

		for(; pre.get(counter).st.before(st); counter++){}

		//文書オブジェクトを生成
		doc = new Document(PageSize.A5, 50, 50, 50, 50); 

		//出力先(アウトプットストリーム)の生成
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("test.pdf");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//アウトプットストリームをPDFWriterに設定
		try {
			pdfwriter = PdfWriter.getInstance(doc, fos);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		doc.open();

		//PdfContentByteの取得
		pdfContentByte = pdfwriter.getDirectContent();

		pdfGraphics2D = new PdfGraphics2D(pdfContentByte,
				doc.getPageSize().getWidth(),
				doc.getPageSize().getHeight());

		//フォントの設定
		try {
			baseFont = BaseFont.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H",BaseFont.NOT_EMBEDDED);
		} catch (DocumentException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while(st.before(ed)){
			grid();

			for(int i = 1; i < 8; i++){			

				pdfContentByte.beginText();
				pdfContentByte.setFontAndSize(baseFont, 8);
				pdfContentByte.setColorFill(new BaseColor(0,0,0));

				String dateSt = st.get(Calendar.MONTH)+1 + "/" + st.get(Calendar.DAY_OF_MONTH) + "（" + getJAPDayOfWeek(st)+ "）";
				pdfContentByte.setTextMatrix(49+(i-1)*53+6, 585);
				pdfContentByte.showText(dateSt);

				pdfContentByte.endText();

				while(counter < pre.size() && pre.get(counter).st.before(st)){
					if(!pre.get(counter).st.equals(pre.get(counter).ed)){
						if(i==7)
							System.out.println();
						drawEvent(i, pre.get(counter));
					}
					counter++;
				}

				st.add(Calendar.DAY_OF_MONTH, 1);
			}
			if(st.before(ed)) doc.newPage();
		}

		pdfGraphics2D.dispose();

		//文章オブジェクト クローズ
		doc.close();

		//PDFWriter クローズ
		pdfwriter.close();
	}

	private static String printCalnedar(Calendar cal) {

		return String.format("%d/%02d/%02d %02d:%02d", 
				cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 
				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));

	}

	private static void drawEvent(int i, ICSparser.PreEvent preEvent) {
		int stHour = preEvent.st.get(Calendar.HOUR_OF_DAY);
		int stMin = preEvent.st.get(Calendar.MINUTE);

		int edHour = preEvent.ed.get(Calendar.HOUR_OF_DAY);
		int edMin = preEvent.ed.get(Calendar.MINUTE);

		int width = (int)Math.floor((edHour-stHour)*4 + (int)(edMin)/15 - (int)(stMin)/15)*10;

		pdfContentByte.setColorFill(new BaseColor(preEvent.color.getRed(), preEvent.color.getGreen(), preEvent.color.getBlue()));
		pdfContentByte.setColorStroke(new BaseColor(0,0,0));
		pdfContentByte.rectangle(49+(i-1)*53, 595-10*((edHour-8)*4 + (int)(edMin/15))-10, 53, width);
		pdfContentByte.fillStroke();

		pdfContentByte.beginText();
		pdfContentByte.setFontAndSize(baseFont, 8);

		pdfContentByte.setColorFill(new BaseColor(0,0,0));

		pdfContentByte.setTextMatrix(49+(i-1)*53, 595-10*((stHour-8)*4 + (int)(stMin/15))-18);
		String time = preEvent.st.get(Calendar.HOUR_OF_DAY) + ":" + preEvent.st.get(Calendar.MINUTE)  
				+ "--" + preEvent.ed.get(Calendar.HOUR_OF_DAY) + ":" + preEvent.ed.get(Calendar.MINUTE);
		pdfContentByte.showText(time);

		pdfContentByte.setTextMatrix(49+(i-1)*53, 595-10*((stHour-8)*4 + (int)(stMin/15))-28);
		pdfContentByte.showText(preEvent.name);

		pdfContentByte.setTextMatrix(49+(i-1)*53, 595-10*((stHour-8)*4 + (int)(stMin/15))-38);
		pdfContentByte.showText("@" + preEvent.location);

		pdfContentByte.endText();

	}

	private static void grid(){

		pdfContentByte.moveTo(20, 595-10);
		pdfContentByte.lineTo(20, 595-590);

		pdfContentByte.moveTo(49, 595-0);
		pdfContentByte.lineTo(49, 595-590);

		for(int i = 1; i < 8; i++){
			pdfGraphics2D.drawLine(49+i*53, 0, 49+i*53, 590);
		}

		for(int i = 1; i < 60; i++){
			if(i == 1 || (i-1)%4 == 0){
				pdfContentByte.setLineDash(1, 0, 0);
				pdfContentByte.setLineWidth(1);
			} else{
				pdfContentByte.setLineDash(1, 2, 0);
				pdfContentByte.setLineWidth(0.5f);
			}
			pdfContentByte.moveTo(20, 595-i*10);
			pdfContentByte.lineTo(420, 595-i*10);
			pdfContentByte.stroke();			
			//			pdfGraphics2D.drawLine(20, i*10, 420, i*10);
		}

		pdfGraphics2D.dispose();

		pdfContentByte.beginText();
		pdfContentByte.setFontAndSize(baseFont, 8);

		for(int i = 1; i < 59; i=i+4){
			pdfContentByte.setTextMatrix(25, doc.getPageSize().getHeight() - (i*10+8));
			pdfContentByte.showText(i/4+8 + ":00");
		}

		pdfContentByte.endText();
	}

	private static String getJAPDayOfWeek(Calendar c){
		switch(c.get(Calendar.DAY_OF_WEEK)){
		case 7: return "土";
		case 1: return "日";
		case 2: return "月";
		case 3: return "火";
		case 4: return "水";
		case 5: return "木";
		case 6: return "金";
		}
		return "　";
	}

}
