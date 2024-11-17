package com.example.babybook.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VaccineNotificationUtil {
    public static class VaccineSchedule {
        public String vaccineName;
        public int daysAfterBirth;

        public VaccineSchedule(String name, int days) {
            this.vaccineName = name;
            this.daysAfterBirth = days;
        }
    }

    private static final List<VaccineSchedule> VACCINE_SCHEDULES = new ArrayList<VaccineSchedule>() {{
        // At birth vaccines (day 0)
        add(new VaccineSchedule("BCG Vaccine", 0));
        add(new VaccineSchedule("Hepatitis B Vaccine", 0));

        // 1.5 months (45 days) vaccines
        add(new VaccineSchedule("Pentavalent Vaccine (DPT-Hep B-HIB) - First Dose", 45));
        add(new VaccineSchedule("Oral Polio Vaccine (OPV) - First Dose", 45));
        add(new VaccineSchedule("Pneumococcal Conjugate Vaccine (PCV) - First Dose", 45));

        // 2.5 months (75 days) vaccines
        add(new VaccineSchedule("Pentavalent Vaccine (DPT-Hep B-HIB) - Second Dose", 75));
        add(new VaccineSchedule("Oral Polio Vaccine (OPV) - Second Dose", 75));
        add(new VaccineSchedule("Pneumococcal Conjugate Vaccine (PCV) - Second Dose", 75));

        // 3.5 months (105 days) vaccines
        add(new VaccineSchedule("Pentavalent Vaccine (DPT-Hep B-HIB) - Third Dose", 105));
        add(new VaccineSchedule("Oral Polio Vaccine (OPV) - Third Dose", 105));
        add(new VaccineSchedule("Pneumococcal Conjugate Vaccine (PCV) - Third Dose", 105));

        // 9 months (270 days) vaccines
        add(new VaccineSchedule("Measles, Mumps, Rubella Vaccine (MMR) - First Dose", 270));
        add(new VaccineSchedule("Inactivated Polio Vaccine (IPV)", 270));

        // 12 months (365 days) vaccines
        add(new VaccineSchedule("Measles, Mumps, Rubella Vaccine (MMR) - Second Dose", 365));
    }};

    public static List<String> getUpcomingVaccineReminders(String birthDateStr) {
        List<String> reminders = new ArrayList<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date birthDate = sdf.parse(birthDateStr);
            Calendar today = Calendar.getInstance();
            Calendar dueDate = Calendar.getInstance();

            // Clear time fields for accurate comparison
            clearTimeFields(today);

            Log.d("VaccineNotificationUtil", "Checking vaccines for birth date: " + birthDateStr);

            for (VaccineSchedule vaccine : VACCINE_SCHEDULES) {
                dueDate.setTime(birthDate);
                dueDate.add(Calendar.DAY_OF_YEAR, vaccine.daysAfterBirth);
                clearTimeFields(dueDate);

                // Check if tomorrow is the vaccine due date
                Calendar dueDateMinus1 = (Calendar) dueDate.clone();
                dueDateMinus1.add(Calendar.DAY_OF_MONTH, -1);

                if (isSameDay(today, dueDateMinus1)) {
                    String reminder = String.format("Reminder: Your child needs to take %s by tomorrow",
                            vaccine.vaccineName);
                    reminders.add(reminder);
                    Log.d("VaccineNotificationUtil", "Added reminder for tomorrow: " + reminder);
                }
            }
        } catch (ParseException e) {
            Log.e("VaccineNotificationUtil", "Error parsing date: " + birthDateStr, e);
        }
        return reminders;
    }

    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private static void clearTimeFields(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}