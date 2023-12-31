package ivanmartinez.simpleStudentsAPI.Service;

import ivanmartinez.simpleStudentsAPI.DTO.StudentFilterDTO;
import ivanmartinez.simpleStudentsAPI.Entity.Student;
import ivanmartinez.simpleStudentsAPI.Repository.StudentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService{

    @Autowired
    private StudentsRepository studentsRepository;

    @Override
    public ResponseEntity<String> createStudent(Student student) {
        System.out.println("Service Post: " + student);
        studentsRepository.save(student);
        return new ResponseEntity<>("Student created successfully", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentsRepository.findAll();

        if(!students.isEmpty()){
            return new ResponseEntity<>(students, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<List<Student>> getFilteredStudents(Optional<String> firstNameParam,
                                                             Optional<String> lastNameParam,
                                                             Optional<String> courseParam) {

        String firstName = firstNameParam.orElse("");
        String lastName = lastNameParam.orElse("");
        String course = courseParam.orElse("");

        System.out.println("Get By Service: " + " FM: "+ firstName
                + " LM: " + lastName + " C: " + course);

        List<Student> students = studentsRepository
                .findByFirstNameContainingAndLastNameContainingAndCourseContaining
                        (firstName, lastName, course);

        if(!students.isEmpty()){
            return new ResponseEntity<>(students, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<String> deleteStudent(Long id) {
        Optional<Student> student = studentsRepository.findById(id);

        if(student.isPresent()){
            studentsRepository.delete(student.get());
            return new ResponseEntity<>("Student with id " + id + " deleted", HttpStatus.ACCEPTED);
        }
        else{
            return new ResponseEntity<>("Student with id " + id + " does not exist", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<String> updateStudent(Student student) {
        Optional<Student> studentById = studentsRepository.findById(student.getId());

        if(studentById.isPresent()) {
            Student getStudentById = studentById.get();

            getStudentById.setFirstName(student.getFirstName());
            getStudentById.setLastName(student.getLastName());
            getStudentById.setCourse(student.getCourse());
            getStudentById.setDateOfBirth(student.getDateOfBirth());
            studentsRepository.save(getStudentById);

            return new ResponseEntity<>("Student Updated",
                    HttpStatus.ACCEPTED);
        }

        return  new ResponseEntity<>
                ("Student with id " + student.getId() + " not found",
                        HttpStatus.NOT_FOUND);
    }

}
