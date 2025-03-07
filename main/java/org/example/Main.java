package org.example;
import java.util.*;
import java.io.*;
class Student {
    private String nume;
    private double medie;

    public Student(String nume) {
        this.nume = nume;
    }

    public String getNume() {
        return nume;
    }

    public double getMedie() {
        return medie;
    }

    public void setMedie(double medie) {
        this.medie = medie;
    }
}

class StudentLicenta extends Student {
    public StudentLicenta(String nume) {
        super(nume);
    }
}

class StudentMaster extends Student {
    public StudentMaster(String nume) {
        super(nume);
    }
}

class Curs<T extends Student> {
    private String nume;
    private int capacitateMaxima;
    private List<T> studentiInscrisi;

    public Curs(String nume, int capacitateMaxima) {
        this.nume = nume;
        this.capacitateMaxima = capacitateMaxima;
        this.studentiInscrisi = new ArrayList<>();
    }

    public String getNume() {
        return nume;
    }

    public int getCapacitateMaxima() {
        return capacitateMaxima;
    }

    public List<T> getStudentiInscrisi() {
        return studentiInscrisi;
    }

    public void adaugaStudent(Student student) {
        studentiInscrisi.add((T) student);
    }
}

class Secretariat {
    private ArrayList<Student> studenti;
    private ArrayList<Curs<? extends Student>> cursuri;
    private Map<String, List<String>> preferinteStudenti;

    public Secretariat() {
        this.studenti = new ArrayList<>();
        this.cursuri = new ArrayList<>();
        this.preferinteStudenti = new HashMap<>();
    }

    public void adaugaStudent(String outputFile, String programStudiu, String numeStudent) {
        boolean gasit = false;
        for (Student student : studenti) {
            if (student.getNume().equals(numeStudent)) {
                gasit = true;
                try (FileWriter fileWriter = new FileWriter(outputFile, true)) {
                    fileWriter.write("***\n");
                    fileWriter.write("Student duplicat: " + numeStudent + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!gasit) {
            Student student;
            if (programStudiu.equals("licenta")) {
                student = new StudentLicenta(numeStudent);
            } else if (programStudiu.equals("master")) {
                student = new StudentMaster(numeStudent);
            } else {
                throw new IllegalArgumentException("Program de studiu necunoscut: " + programStudiu);
            }
            studenti.add(student);
        }
    }

    public void adaugaCurs(String programStudiu, String numeCurs, int capacitateMaxima) {
        if (programStudiu.equals("licenta")) {
            Curs<StudentLicenta> cursLicenta = new Curs<>(numeCurs, capacitateMaxima);
            cursuri.add(cursLicenta);
        } else if (programStudiu.equals("master")) {
            Curs<StudentMaster> cursMaster = new Curs<>(numeCurs, capacitateMaxima);
            cursuri.add(cursMaster);
        } else {
            throw new IllegalArgumentException("Program de studiu necunoscut: " + programStudiu);
        }
    }

    public void citesteMediile(String folderTest) {
        try {
            String[] fisiereNote = new java.io.File(folderTest)
                    .list((dir, name) -> name.startsWith("note_"));
            if (fisiereNote != null) {
                for (String fisierNote : fisiereNote) {
                    String caleFisier = folderTest + "/" + fisierNote;
                    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(caleFisier))) {
                        String linie;
                        while ((linie = bufferedReader.readLine()) != null) {
                            String[] split = linie.split(" - ");
                            if (split.length == 2) {
                                String numeStudent = split[0];
                                double medie = Double.parseDouble(split[1]);
                                for (Student student : studenti) {
                                    if (student.getNume().equals(numeStudent)) {
                                        student.setMedie(medie);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void posteazaMediile(String outputFile) {
        List<Student> studentiOrdonati = new ArrayList<>(studenti);
        Comparator<Student> comparator = Comparator
                .comparingDouble(Student::getMedie)
                .reversed()
                .thenComparing(Student::getNume);
        studentiOrdonati.sort(comparator);
        try (FileWriter fileWriter = new FileWriter(outputFile, true)) {
            fileWriter.write("***\n");
            for (Student student : studentiOrdonati) {
                fileWriter.write(student.getNume() + " - " + student.getMedie() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void contestatie(String numeStudent, double nouaMedie) {
        try {
            for (Student student : studenti) {
                if (student.getNume().equals(numeStudent)) {
                    student.setMedie(nouaMedie);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void adaugaPreferinte(String numeStudent, String preferinte) {
        try {
            if (preferinteStudenti.containsKey(numeStudent)) {
                List<String> existingPreferences = preferinteStudenti.get(numeStudent);
                String[] words = preferinte.split("\\s+");
                existingPreferences.addAll(Arrays.asList(words));
            } else {
                String[] words = preferinte.split("\\s+");
                List<String> preferinteList = new ArrayList<>(Arrays.asList(words));
                preferinteStudenti.put(numeStudent, preferinteList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void repartizeaza() {
        try {
            Collections.sort(studenti, Comparator.comparing(Student::getMedie).reversed());
            boolean atribuit;
            for (Student student : studenti) {
                atribuit = false;
                if (preferinteStudenti.containsKey(student.getNume())) {
                    List<String> preferinte = preferinteStudenti.get(student.getNume());
                    for (String cursNume : preferinte) {
                        if (!atribuit) {
                            for (Curs<? extends Student> curs : cursuri) {
                                if (curs.getNume().equals((cursNume))) {
                                    if (curs.getStudentiInscrisi().size() < curs.getCapacitateMaxima()) {
                                        curs.adaugaStudent(student);
                                        atribuit = true;
                                    } else {
                                        if (!curs.getStudentiInscrisi().isEmpty()) {
                                            Student stud = curs.getStudentiInscrisi().get(curs.getStudentiInscrisi().size() - 1);
                                            if (stud.getMedie() == student.getMedie()) {
                                                curs.adaugaStudent(student);
                                                atribuit = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            System.err.println("NullPointerException in repartizeaza: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected exception in repartizeaza: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void posteazaCurs(String outputFile, String numeCurs) {
        try (FileWriter fileWriter = new FileWriter(outputFile, true)) {
            for (Curs<? extends Student> curs : cursuri) {
                if (curs.getNume().equals(numeCurs)) {
                    fileWriter.write("***\n");
                    fileWriter.write(numeCurs + " (" + curs.getCapacitateMaxima() + ")\n");
                    Set<Student> sortedStudents = new TreeSet<>((s1, s2) -> s1.getNume().compareTo(s2.getNume()));
                    sortedStudents.addAll(curs.getStudentiInscrisi());
                    for (Student student : sortedStudents) {
                        fileWriter.write(student.getNume() + " - " + student.getMedie() + "\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void posteazaStudent(String outputFile, String numeStudent) {
        try {
            try (FileWriter fileWriter = new FileWriter(outputFile, true)) {
                fileWriter.write("***\n");
                for (Student student : studenti) {
                    if (student.getNume().equals(numeStudent)) {
                        String cicluStudii = (student instanceof StudentLicenta) ? "Licenta" : "Master";
                        fileWriter.write("Student " + cicluStudii + ": ");
                        fileWriter.write(student.getNume() + " - " + student.getMedie() + " - ");
                        Curs<? extends Student> cursAsignat = cursuri.stream()
                                .filter(c -> c.getStudentiInscrisi().contains(student))
                                .findFirst()
                                .orElse(null);
                        if (cursAsignat != null) {
                            fileWriter.write(cursAsignat.getNume());
                        } else {
                            fileWriter.write("Niciun curs asignat");
                        }
                        fileWriter.write("\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        try {
            String folderTest = "src/main/resources/" + args[0];
            String inputFile = folderTest + "/" + args[0] + ".in";
            String outputFile = folderTest + "/" + args[0] + ".out";
            Secretariat secretariat = new Secretariat();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] comanda = line.split(" ");
                    switch (comanda[0]) {
                        case "adauga_student":
                            System.out.println("adauga student");
                            secretariat.adaugaStudent(outputFile, comanda[2], comanda[4]);
                            break;
                        case "adauga_curs":
                            System.out.println("adauga curs");
                            secretariat.adaugaCurs(comanda[2], comanda[4], Integer.parseInt(comanda[6]));
                            break;
                        case "citeste_mediile":
                            System.out.println("citeste mediile");
                            secretariat.citesteMediile(folderTest);
                            break;
                        case "posteaza_mediile":
                            System.out.println("posteaza mediile");
                            secretariat.posteazaMediile(outputFile);
                            break;
                        case "contestatie":
                            System.out.println("contestatie");
                            secretariat.contestatie(comanda[2], Double.parseDouble(comanda[4]));
                            break;
                        case "adauga_preferinte":
                            System.out.println("adauga preferinte");
                            String preferinte = "";
                            for (int i = 4; i < comanda.length; i+=2) {
                               preferinte = preferinte.concat(comanda[i] + " ");
                            }
                            secretariat.adaugaPreferinte(comanda[2], preferinte.substring(0, preferinte.length() - 1));
                            break;
                        case "repartizeaza":
                            System.out.println("repartizeaza");
                            secretariat.repartizeaza();
                            break;
                        case "posteaza_curs":
                            System.out.println("posteaza curs");
                            secretariat.posteazaCurs(outputFile, comanda[2]);
                            break;
                        case "posteaza_student":
                            System.out.println("posteaza student");
                            secretariat.posteazaStudent(outputFile, comanda[2]);
                            break;
                        default:
                            System.out.println("Comanda necunoscuta: " + comanda[0]);
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

