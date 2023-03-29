import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
/**
 *
 * This is just a demo for you, please run it on JDK17 (some statements may be not allowed in lower version).
 * This is just a demo, and you can extend and implement functions
 * based on this demo, or implement it in a different way.
 */
public class OnlineCoursesAnalyzer {

    List<Course> courses = new ArrayList<>();
    public OnlineCoursesAnalyzer(String datasetPath) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4], info[5],
                        Integer.parseInt(info[6]), Integer.parseInt(info[7]), Integer.parseInt(info[8]),
                        Integer.parseInt(info[9]), Integer.parseInt(info[10]), Double.parseDouble(info[11]),
                        Double.parseDouble(info[12]), Double.parseDouble(info[13]), Double.parseDouble(info[14]),
                        Double.parseDouble(info[15]), Double.parseDouble(info[16]), Double.parseDouble(info[17]),
                        Double.parseDouble(info[18]), Double.parseDouble(info[19]), Double.parseDouble(info[20]),
                        Double.parseDouble(info[21]), Double.parseDouble(info[22]));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //1
    public Map<String, Integer> getPtcpCountByInst() {
        Map<String, Integer> map = courses.stream().collect(Collectors.groupingBy(Course::getInstitution, Collectors.summingInt(Course::getParticipants)));
        Map<String, Integer> result = new TreeMap<>(Comparator.naturalOrder());
        map.forEach((key, value) -> result.put(key, Math.toIntExact(value)));
       // System.out.println(result);
        return result;
    }

    //2
    public Map<String, Integer> getPtcpCountByInstAndSubject() {
        Map<String, Integer> result = courses.stream()
                .collect(Collectors.groupingBy(course -> course.getInstitution() + "-" + course.getSubject(),
                        Collectors.summingInt(Course::getParticipants)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return result;
    }

    //3
    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        Map<String, List<List<String>>> result = new TreeMap<>();
        for(Course c : courses){
            String[] instructor = c.instructors.split(", ");
            if(instructor.length == 1){
                if(result.containsKey(instructor[0])){
                    if(!result.get(instructor[0]).get(0).contains(c.title)){
                        result.get(instructor[0]).get(0).add(c.title);
                    }
                }else {
                    List<String> independent = new ArrayList<String> ();
                    List<String> coDeveloped = new ArrayList<String> ();
                    independent.add(c.title);
                    result.put(instructor[0],List.of(independent,coDeveloped));
                }
            }else if(instructor.length > 1){
                for (String i : instructor){
                    if(result.containsKey(i)){
                        if(!result.get(i).get(1).contains(c.title)){
                            result.get(i).get(1).add(c.title);
                        }
                    }else {
                        List<String> independent = new ArrayList<String> ();
                        List<String> coDeveloped = new ArrayList<String> ();
                        coDeveloped.add(c.title);
                        result.put(i,List.of(independent,coDeveloped));
                    }
                }
            }
        }
        for (List<List<String>> lists : result.values()) {
            Collections.sort(lists.get(0));
            Collections.sort(lists.get(1));
        }
        return result;
    }
    //4
    public List<String> getCourses(int topK, String by) {
        if (by.equals("hours")) {
            Collections.sort(courses, (o1, o2) -> {
                if (o1.getTotalHours() == o2.getTotalHours()) {
                    return o1.title.compareTo(o2.title);
                } else {
                    return (int)(o2.getTotalHours() - o1.getTotalHours());
                }
            });
        }else if (by.equals("participants")) {
            Collections.sort(courses, (o1, o2) -> {
                if (o1.participants == o2.participants) {
                    return o1.title.compareTo(o2.title);
                } else {
                    return o2.participants - o1.participants;
                }
            });
        }
        List<String> result = new ArrayList<> ();
        Set<String> set = new HashSet<> ();
        int cnt = 0;
        for (Course c : courses){
            if(set.add(c.title)){
                result.add(c.title);
                cnt++;
            }
            if(cnt >= topK){
                break;
            }
        }
        return result;
    }

    //5
    public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
        List<String> temp = courses.stream().filter(c ->
                        c.getSubject().toLowerCase().contains(courseSubject.toLowerCase())
                                && c.getPercentAudited() >= percentAudited
                                && c.getTotalHours() <= totalCourseHours)
                .map(Course::getTitle)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
        Set<String> set = new HashSet<> ();
        List<String> result = new ArrayList<> ();
        for (String str : temp){
            if(set.add(str)){
                result.add(str);
            }
        }
        return result;
    }

    //6
    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {

        return null;
    }

}

class Course {
    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;
    public Course(String institution, String number, Date launchDate,
                  String title, String instructors, String subject,
                  int year, int honorCode, int participants,
                  int audited, int certified, double percentAudited,
                  double percentCertified, double percentCertified50,
                  double percentVideo, double percentForum, double gradeHigherZero,
                  double totalHours, double medianHoursCertification,
                  double medianAge, double percentMale, double percentFemale,
                  double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) title = title.substring(1);
        if (title.endsWith("\"")) title = title.substring(0, title.length() - 1);
        this.title = title;
        if (instructors.startsWith("\"")) instructors = instructors.substring(1);
        if (instructors.endsWith("\"")) instructors = instructors.substring(0, instructors.length() - 1);
        this.instructors = instructors;
        if (subject.startsWith("\"")) subject = subject.substring(1);
        if (subject.endsWith("\"")) subject = subject.substring(0, subject.length() - 1);
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }
    public String getInstitution()
    {
        return institution;
    }

    public int getParticipants() {
        return participants;
    }

    public String getSubject() {
        return subject;
    }

    public String getInstructors() {
        return instructors;
    }

    public double getPercentCertified() {
        return percentCertified;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public double getPercentAudited() {
        return percentAudited;
    }

    public String getTitle() {
        return title;
    }
}
