package com.example.week9;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity{

    Context context = null;
    theatreArray ar = new theatreArray();
    Spinner spinner;
    ListView listView;
    EditText startTime;
    EditText endTime;
    EditText date;
    String day;
    String st;
    String end;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        spinner = (Spinner) findViewById(R.id.spinner);
        listView = (ListView) findViewById(R.id.listview);
        startTime = (EditText) findViewById(R.id.startTime);
        endTime = (EditText) findViewById(R.id.endTime);
        date = (EditText) findViewById(R.id.date);




        getTheatres();
        initializeUI();


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                date.setText(date.getText().toString());
                startTime.setText(startTime.getText().toString());
                endTime.setText(endTime.getText().toString());

                st = startTime.getText().toString();
                end = endTime.getText().toString();
                day = date.getText().toString();

                if (day.contains(".") || st.contains(":") || end.contains(":")){
                    selectionManagement();
                } else {
                    getSchedule();
                }



            }
        });
    }




    public void getTheatres(){
        try{
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String urlString = "https://www.finnkino.fi/xml/TheatreAreas/"; //TODO
            Document doc = builder.parse(urlString);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getDocumentElement().getElementsByTagName("TheatreArea");


            for (int i = 0; i < nList.getLength() ; i++){
                Node node = nList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    String name = element.getElementsByTagName("Name").item(0).getTextContent();
                    String Id = element.getElementsByTagName("ID").item(0).getTextContent();
                    ar.addList(name, Id);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } finally {
            System.out.println("##########DONE##########");
        }
    }



    public void initializeUI(){

        ArrayList list = ar.getListNames();

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<Theatre> adapter = new ArrayAdapter<Theatre>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }




    public void getSchedule(){
        ArrayList <String> datalist = new ArrayList<String>();

        //Initialization to get start time
        SimpleDateFormat ip = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat op = new SimpleDateFormat("HH:mm dd.MM.yyyy");

        Date dt = null;

        //Get today's date
        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String Date = df.format(d);

        //Getting the ID of the chosen theatre
        String choice = spinner.getSelectedItem().toString();
        String Id = ar.getId(choice);

        //Building the document
        try{
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String urlString = "https://www.finnkino.fi/xml/Schedule/?area="+Id+"&dt="+Date; //TODO
            Document doc = builder.parse(urlString);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getDocumentElement().getElementsByTagName("Show");
            System.out.println(nList.getLength());


            for (int i = 0; i < nList.getLength() ; i++){
                Node node = nList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    String Title = element.getElementsByTagName("Title").item(0).getTextContent();
                    String s = element.getElementsByTagName("dttmShowStart").item(0).getTextContent();
                    try{
                        dt = ip.parse(s);
                    } catch (ParseException e){
                        e.printStackTrace();
                    }
                    String start = op.format(dt);

                    String a = Title + "\nStarts at: " + start;
                    datalist.add(a);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } finally {
            System.out.println("##########DONE##########");
        }

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, datalist);
        listView.setAdapter(itemsAdapter);

    }

    public void selectionManagement(){

        //Initialization to get start time
        SimpleDateFormat ip = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat op = new SimpleDateFormat("HH:mm dd.MM.yyyy");

        ArrayList <String> datalist2 = new ArrayList<String>();

        Date dt = null;
        String str = null;
        String ed = null;

        //Getting the ID of the chosen theatre
        String choice = spinner.getSelectedItem().toString();
        String Id = ar.getId(choice);

        str = day + " " + st;
        ed = day + " " + end;

        //Building the document
        try{
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String urlString = "https://www.finnkino.fi/xml/Schedule/?area="+Id+"&dt="+day; //TODO
            System.out.println("URL: " + urlString);
            Document doc = builder.parse(urlString);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            System.out.println(day);

            NodeList nList = doc.getDocumentElement().getElementsByTagName("Show");
            System.out.println(nList.getLength());


            for (int i = 0; i < nList.getLength() ; i++){
                Node node = nList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    String Title = element.getElementsByTagName("Title").item(0).getTextContent();
                    String s = element.getElementsByTagName("dttmShowStart").item(0).getTextContent();

                    try{
                        dt = ip.parse(s);
                    } catch (ParseException e){
                        e.printStackTrace();
                    }

                    SimpleDateFormat ip1 = new SimpleDateFormat("dd.MM.yyy HH:mm");
                    Date sttime = null;
                    Date edtime = null;
                    try{
                        sttime = ip1.parse(str);
                        edtime = ip1.parse(ed);
                    } catch (ParseException e){

                    }
                    String start = op.format(dt);
                    String a = Title + "\nStarts at: " + start;

                    if (sttime == null || edtime == null) {
                        datalist2.add(a);
                    } else {

                        if ((sttime.compareTo(dt) < 0) && (edtime.compareTo(dt) > 0)) {
                            datalist2.add(a);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } finally {
            System.out.println("##########DONE##########");
        }

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, datalist2);
        listView.setAdapter(itemsAdapter);

    }

}