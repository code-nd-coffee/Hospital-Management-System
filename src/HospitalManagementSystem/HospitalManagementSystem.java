package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital_management_system";
    private static final String user = "root";
    private static final String password = "7266194@MySql";
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Scanner scanner = new Scanner(System.in);
            Doctors doctors = new Doctors(connection);
            Patients patients = new Patients(connection, scanner);

            while (true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.print("Enter Your Choice: ");
                int choice = scanner.nextInt();
                switch (choice){
                    case 1:
                        patients.addPatient();
                        System.out.println();
                        break;

                    case 2:
                        patients.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        doctors.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        bookAppointment(patients, doctors, connection, scanner);
                        System.out.println();
                        break;
                    case 5:
                        System.out.println("THANK YOU FOR USING HOSPITAL MANAGEMENT SYSTEM");
                        return;
                    default:
                        System.out.println("Enter Valid Choice!");
                        break;
                }
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }
    public static void bookAppointment(Patients patients, Doctors doctors, Connection connection, Scanner scanner){
        System.out.print("Enter Patient Id: ");
        int patient_id = scanner.nextInt();
        System.out.print("Enter Doctor Id: ");
        int doctor_id = scanner.nextInt();
        System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        if (patients.getPatientById(patient_id) && doctors.getDoctorById(doctor_id)){
            if (checkDoctorAvilability(doctor_id, appointmentDate, connection)){
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patient_id);
                    preparedStatement.setInt(2, doctor_id);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0){
                        System.out.println("Appointment Booked");
                    } else {
                        System.out.println("Failed To Book Appointment");
                    }
                } catch (SQLException e){
                    e.printStackTrace();
                }

            } else {
                System.out.println("Doctor not available on this date!");
            }
        } else {
            System.out.println("Either Doctor Or Patient Doesn't Exists!");
        }
    }

    public static boolean checkDoctorAvilability(int doctor_id, String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctor_id);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int count = resultSet.getInt(1);
                if (count == 0){
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;

    }
}
