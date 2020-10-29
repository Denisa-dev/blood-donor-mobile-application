package com.example.firestoredatabase;

import com.example.firestoredatabase.Model.Judet;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Common {
    public static final String KEY_ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KEY_CENTRU = "CENTRU_SALVAT";
    public static final String KEY_DISPLAY_TIME_SLOT = "DISPLAY_TIME_SLOT";
    public static final String KEY_STEP = "STEP";
    public static final String KEY_CENTRE_SELECTED = "CENTRE_SELECTED";
    public static final int TIME_SLOT_TOTAL = 12;
    public static final String KEY_CENTRE_LOAD_DONE = "CENTRE_LOAD_DONE";
    public static final Object DISABLE_TAG = "DISABLE";
    public static final String KEY_TIME_SLOT = "TIME_SLOT" ;
    public static final String KEY_CONFIRM = "COFIRM_BOOKING";
    public static Judet currentJudet;
    public static int step = 0;
    public static int noBook = 0;
    public static int docs = 0;
    public static int tab = 0;
    public static long eventID;
    public static int noBookingsF[];
    public static String grupSange;
    public static String docIDPerson = "";
    public static int noBookingsM[];
    public static Boolean notifyFlag = false;
    public static Boolean bookCheck = false;
    public static Integer noBookings = 0;
    public static Long mySlot;
    public static String judet;
    public static String judetTab, centruTab;
    public static String currentCentre;
    public static int currentTimeSlot = -1;
    public static Calendar currentDate = Calendar.getInstance();
    public static Calendar dateDonation = Calendar.getInstance();
    public static Calendar editDateDonation = Calendar.getInstance();
    public static Calendar actualDate = Calendar.getInstance();
    public static SimpleDateFormat simpleFormatDate = new SimpleDateFormat("dd_MM_yyyy");
    public static Boolean getNotifyAllow = true;

    public static String matchCentru(String judet){
        switch (judet)
        {
            case "Timiș":
                return "CTS Timișoara";

            case "Iași":
                return "CTS Iași";

            case "Neamț":
                return "CTS Piatra Neamț";

            case "Argeș":
                return "CTS Pitești";

            case "Prahova":
                return "CTS Ploiești";

            case "Caraș-Severin":
                return "CTS Reșița";

            case "Mureș":
                return "CTS Tg. Mureș";

            case "București":
                return "CTSM București";

            default:
                return "CTS Timișoara";
        }
    }

    public static String matchJudet(String centru){
        switch (centru)
        {
            case "CTS Alba":
                return "Alba";

            case "CTS Alexandria":
                return "Teleorman";

            case "CTS Arad":
                return "Arad";

            case "CTS Bacău":
                return "Bacău";

            case "CTS Baia Mare":
                return "Maramureș";

            case "CTS Bârlad":
                return "Vaslui";

            case "CTS Bistriţa":
                return "Bistrița-Năsăud";

            case "CTS Botoşani":
                return "Botoșani";

            case "CTS Braşov":
                return "Brașov";

            case "CTS Brăila":
                return "Brăila";

            case "CTS Buzău":
                return "Buzău";

            case "CTS Călăraşi":
                return "Călărași";

            case "CTS Cluj":
                return "Cluj";

            case "CTS Constanţa":
                return "Constanța";

            case "CTS Craiova":
                return "Dolj";

            case "CTS Târgovişte":
                return "Dâmbovița";

            case "CTS Deva":
                return "Hunedoara";

            case "CTS Petroșani":
                return "Hunedoara";

            case "CTS Dr.Tr. Severin":
                return "Mehedinți";

            case "CTS Focşani":
                return "Vrancea";

            case "CTS Galaţi":
                return "Galați";

            case "CTS Giurgiu":
                return "Giurgiu";

            case "CTS Iaşi":
                return "Iași";

            case "CTS Mc. Ciuc":
                return "Harghita";

            case "CTS Oradea":
                return "Bihor";

            case "CTS Piatra Neamţ":
                return "Neamț";

            case "CTS Piteşti":
                return "Argeș";

            case "CTS Câmpulung Muscel":
                return "Argeș";

            case "CTS Ploieşti":
                return "Prahova";

            case "CTS Reşiţa":
                return "Caraș-Severin";

            case "CTS Rm. Vâlcea":
                return "Vâlcea";

            case "CTS Satu Mare":
                return "Satu Mare";

            case "CTS Sf. Gheorghe":
                return "Covasna";

            case "CTS Sibiu":
                return "Sibiu";

            case "CTS Slatina":
                return "Olt";

            case "CTS Slobozia":
                return "Ialomița";

            case "CTS Suceava":
                return "Suceava";

            case "CTS Tg. Jiu":
                return "Gorj";

            case "CTS Tg. Mureş":
                return "Mureș";

            case "CTS Timişoara":
                return "Timiș";

            case "CTS Tulcea":
                return "Tulcea";

            case "CTS Zalău":
                return "Sălaj";

            case "CTSM Bucureşti":
                return "București";

            case "PRS Spitalul de Urgență Floreasca":
                return "București";

            case "PRS Institutul Clinic Fundeni":
                return "București";

            case "PRS Spitalul Militar":
                return "București";

            case "PRS Spitalul Universitar de Urgență":
                return "București";

            default:
                return "București";
        }
    }

    public static String converTimeSlotToString(int slot) {
        switch (slot)
        {
            case 0:
                return "8:00-8:30";
            case 1:
                return "8:30-9:00";
            case 2:
                return "9:00-9:30";
            case 3:
                return "9:30-10:00";
            case 4:
                return "10:00-10:30";
            case 5:
                return "10:30-11:00";
            case 6:
                return "11:00-11:30";
            case 7:
                return "11:30-12:00";
            case 8:
                return "12:00-12:30";
            case 9:
                return "12:30-13:00";
            case 10:
                return "13:00-13:30";
            case 11:
                return "13:30-14:00";
                default:
                    return "Indisponibil";
        }
    }
}
