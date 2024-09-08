import React, { useState, useEffect } from 'react';
import './App.css';

function RegisterForm() {
  const [firstName, setFirstName] = useState('Duc Thang');
  const [lastName, setLastName] = useState('Le');
  const [email, setEmail] = useState('leductha@fel.cvut.cz');
  const [phoneNumber, setPhoneNumber] = useState('778031875');
  const [birthDate, setBirthDate] = useState('2003-09-06');
  const [password, setPassword] = useState('123');
  const [roleKeypass, setRoleKeypass] = useState('studentKeyPass');
  const [regSuccessMessage, setRegSuccessMessage] = useState('');
  const [regErrorMessage, setRegErrorMessage] = useState('');

  async function handleSubmit(event) {
    event.preventDefault();

    const data = {
      firstName,
      lastName,
      email,
      phoneNumber,
      birthDate,
      password,
      roleKeypass
    };

    try {
      const response = await fetch('http://localhost:8080/rest/person', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });
      if (!response.ok) {
        throw new Error(`Registration failed with status: ${response.status}`);
      }
      setRegSuccessMessage('Registration successful');
      setRegErrorMessage('');
    } catch (error) {
      setRegErrorMessage(error.message);
      setRegSuccessMessage('');
    }
  }

  return (
      <div>
        <form onSubmit={handleSubmit}>
          <label htmlFor="firstName">First Name:</label><br/>
          <input type="text" id="firstName" name="firstName" value={firstName}
                 onChange={(e) => setFirstName(e.target.value)}/><br/>
          <label htmlFor="lastName">Last Name:</label><br/>

          <input type="text" id="lastName" name="lastName" value={lastName}
                 onChange={(e) => setLastName(e.target.value)}/>
          <br/>
          <label htmlFor="email">Email:</label><br/>
          <input type="email" id="email" name="email" value={email} onChange={(e) => setEmail(e.target.value)}/><br/>
          <label htmlFor="phoneNumber">Phone Number:</label><br/>
          <input type="text" id="phoneNumber" name="phoneNumber" value={phoneNumber}
                 onChange={(e) => setPhoneNumber(e.target.value)}/>

          <br/>
          <label htmlFor="birthDate">Birth Date:</label><br/>
          <input type="date" id="birthDate" name="birthDate" value={birthDate}
                 onChange={(e) => setBirthDate(e.target.value)}/>

          <br/>

          <label htmlFor="regPassword">Password:</label><br/>
          <input type="password" id="regPassword" name="password" value={password}
                 onChange={(e) => setPassword(e.target.value)}/><br/>
          <label htmlFor="roleKeypass">Role Key Pass:</label><br/>
          <input type="text" id="roleKeypass" name="roleKeypass" value={roleKeypass}
                 onChange={(e) => setRoleKeypass(e.target.value)}/><br/>
          <input type="submit" value="Register"/>
        </form>

        <div className={"successMessage"}>{regSuccessMessage}</div>
        <div className={"errorMessage"}>{regErrorMessage}</div>
      </div>
  );
}

function LoginForm() {
  const [username, setUsername] = useState('leductha');
  const [password, setPassword] = useState('123');
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  async function handleSubmit(event) {
    event.preventDefault();

    try {
      const data = await login(username, password);
      if (data) {
        setSuccessMessage('Login successful');
        setErrorMessage('');
      } else {
        throw new Error('Login failed. Please check your username and password.');
      }
    } catch (error) {
      setErrorMessage("Login failed. Please check your username and password.");
      setSuccessMessage('');
    }
  }

  async function login(username, password) {
    try {
      const response = await fetch('http://localhost:8080/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`,
        credentials: 'include',
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      else if (response.ok) {
        return response.json();
      } else {
        throw new Error('Login failed.');
      }
    } catch (error) {
      console.error('Error during login:', error);
      throw error;
    }
  }


  return (
      <div>
        <form onSubmit={handleSubmit}>
          <label htmlFor="username">Username:</label><br />
          <input type="text" id="username" name="username" value={username} onChange={(e) => setUsername(e.target.value)} /><br />
          <label htmlFor="password">Password:</label><br />
          <input type="password" id="password" name="password" value={password} onChange={(e) => setPassword(e.target.value)} /><br />
          <input type="submit" value="Login" />
        </form>

        <div className={"successMessage"}>{successMessage}</div>
        <div className={"errorMessage"}>{errorMessage}</div>
      </div>
  );
}

function Dashboard() {
  const [courses, setCourses] = useState([]);
  const [schedule, setSchedule] = useState([]);
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [errorRevertMessage, setErrorRevertMessage] = useState('');
  const [successRevertMessage, setSuccessRevertMessage] = useState('');

  useEffect(() => {
    fetchCourses();
    fetchSchedule();
  }, []);

  const fetchCourses = async () => {
    try {
      const response = await fetch('http://localhost:8080/student/course/next', {
        credentials: 'include', // Include cookies
      });

      const data = await response.json();

      if (response.ok) {
        setCourses(data);
      } else {
        setErrorMessage(data.message);
      }
    } catch (error) {
      console.error('Error fetching courses:', error);
      setErrorMessage('Failed to fetch courses.');
    }
  };

  const fetchSchedule = async () => {
    try {
      const response = await fetch(`http://localhost:8080/student/schedule`, {
        credentials: 'include', // Include cookies
      });
      const data = await response.json();
      if (response.ok) {
        setSchedule(data);
        setErrorMessage('');
      } else {
        setSchedule([]);
      }
    } catch (error) {
      console.error('Error fetching schedule:', error);
      setErrorMessage('Failed to fetch schedule.');
      setSchedule([]);
    }
  };

  const enrollInParallel = async (parallelId) => {
    try {
      const response = await fetch(`http://localhost:8080/student/enroll/${parallelId}`, {
        method: 'POST',
        credentials: 'include',
      });

      if (response.ok) {
        setErrorMessage('');
        setSuccessMessage('Enrollment successful.');
        setSuccessRevertMessage('');
        fetchSchedule();
      } else {
        const errorData = await response.json();
        setErrorMessage(errorData.message);
        setSuccessMessage('');
      }
    } catch (error) {
      console.error('Error enrolling in parallel:', error);
      setErrorMessage('Failed to enroll in parallel.');
    }
  };

  const revertFromParallel = async (parallelId) => {
    try {
      const response = await fetch(`http://localhost:8080/student/enroll/${parallelId}`, {
        method: 'DELETE',
        credentials: 'include',
      });
      if (response.ok) {
        setSuccessRevertMessage('Enrollment reverted.');
        setErrorRevertMessage('');
        setSuccessMessage('');
        setErrorMessage('');
        fetchSchedule();
      } else {
        throw new Error('Failed to revert enrollment.');
      }
    } catch (error) {
      console.error('Error reverting enrollment:', error);
      setErrorRevertMessage('Failed to revert enrollment.');
      setSuccessRevertMessage('');
    }
  };

  return (
      <div>
        {errorRevertMessage && <div className={"errorMessage"}>{errorRevertMessage}</div>}
        {successRevertMessage && <div className={"successMessage"}>{successRevertMessage}</div>}
        <ViewScheduleForSemester
            schedule={schedule}
            revertFromParallel={revertFromParallel}
        />

        <ListAllParallelsForNextSemester
            courses={courses}
            enrollInParallel={enrollInParallel}
            fetchCourses={fetchCourses}
            error={errorMessage}
            success={successMessage}
        />
      </div>
  );
}

function ViewScheduleForSemester({ schedule, revertFromParallel }) {
  return (
      <div>
        <h3 className={"actionName"}>My Schedule</h3>

        <table className="table">
          <thead>
          <tr>
            <th></th>
            <th>07:30 - 09:00</th>
            <th>09:15 - 10:45</th>
            <th>11:00 - 12:30</th>
            <th>12:45 - 14:15</th>
            <th>14:30 - 16:00</th>
            <th>16:15 - 17:45</th>
            <th>18:00 - 19:30</th>
          </tr>
          </thead>
          <tbody>
          {['MON', 'TUE', 'WED', 'THU', 'FRI'].map((day) => (
              <tr key={day}>
                <td>{day}</td>
                {['07:30 - 09:00', '09:15 - 10:45', '11:00 - 12:30', '12:45 - 14:15', '14:30 - 16:00', '16:15 - 17:45', '18:00 - 19:30'].map((slot) => (
                    <td key={slot}>
                      {schedule
                          .filter(
                              (parallelDto) =>
                                  parallelDto.dayOfWeek === day && parallelDto.timeSlot === slot
                          )
                          .map((parallelDto, index) => (
                              <div key={index}>
                                <div>{parallelDto.courseCode}</div>
                                <div>{parallelDto.teacherName}</div>
                                <div>{parallelDto.classroomCode}</div>
                                <div>
                                  <button onClick={() => revertFromParallel(parallelDto.id)}>Revert</button>
                                </div>
                              </div>
                          ))}
                    </td>
                ))}
              </tr>
          ))}
          </tbody>
        </table>
      </div>
  );
}

function ListAllParallelsForNextSemester({ courses, enrollInParallel, error, success}) {
  return (
      <div>
        <h3 className={"actionName"}>List Parallels For Next Semester</h3>
        {error && <div className={"errorMessage"}>{error}</div>}
        {success && <div className={"successMessage"}>{success}</div>}

        {courses.length > 0 && (
            <table className="table">
              <thead>
              <tr>
                <th>ID</th>
                <th>Time Slot</th>
                <th>Day</th>
                <th>Course</th>
                <th>Course Code</th>
                <th>Teacher</th>
                <th>Classroom</th>
                <th></th>
              </tr>
              </thead>
              <tbody>
              {courses.map((parallelDto, index) => (
                  <tr key={index}>
                    <td>{parallelDto.id}</td>
                    <td>{parallelDto.timeSlot}</td>
                    <td>{parallelDto.dayOfWeek}</td>
                    <td>{parallelDto.courseName}</td>
                    <td>{parallelDto.courseCode}</td>
                    <td>{parallelDto.teacherName}</td>
                    <td>{parallelDto.classroomCode}</td>
                    <td>
                      <button onClick={() => enrollInParallel(parallelDto.id)}>Enroll</button>
                    </td>
                  </tr>
              ))}
              </tbody>
            </table>
        )}
      </div>
  );
}

function ListParallelsForCourseNextSemester() {
  const [courseCode, setCourseCode] = useState('');
  const [parallels, setParallels] = useState([]);
  const [errorMessage, setErrorMessage] = useState('');

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      const response = await fetch(`http://localhost:8080/student/parallel/${courseCode}`, {
        credentials: 'include', // Include cookies
      });
      const data = await response.json();
      if (response.ok) {
        setParallels(data);
        setErrorMessage('');
      } else {
        setErrorMessage(data.message);
        setParallels([]);
      }
    } catch (error) {
      console.error('Error fetching parallels:', error);
      setErrorMessage('Failed to fetch parallels.');
      setParallels([]);

    }
  };

  return (
      <div>
        <h2 className={"actionName"}>List Parallels For Course</h2>
        <form onSubmit={handleSubmit}>
          <label htmlFor="courseCode">Course Code:</label><br/>
          <input type="text" id="courseCode" value={courseCode} onChange={(e) => setCourseCode(e.target.value)}/><br/>
          <input type="submit" value="List Parallels"/>
        </form>

        {errorMessage && <div className={"errorMessage"}>{errorMessage}</div>}

        {parallels.length > 0 && (
            <table className="table">
              <thead>
              <tr>
                <th>ID</th>
                <th>Time Slot</th>
                <th>Day</th>
                <th>Course</th>
                <th>Course Code</th>
                <th>Teacher</th>
                <th>Classroom</th>
              </tr>
              </thead>
              <tbody>
              {parallels.map((parallelDto, index) => (
                  <tr key={index}>
                    <td>{parallelDto.id}</td>
                    <td>{parallelDto.timeSlot}</td>
                    <td>{parallelDto.dayOfWeek}</td>
                    <td>{parallelDto.courseName}</td>
                    <td>{parallelDto.courseCode}</td>
                    <td>{parallelDto.teacherName}</td>
                    <td>{parallelDto.classroomCode}</td>
                  </tr>
              ))}
              </tbody>
            </table>
        )}

      </div>
  );
}

function ViewEnrollmentReport() {
  const [report, setReport] = useState([]);
  const [errorMessage, setErrorMessage] = useState('');

    const fetchReport = async () => {
      try {
        const response = await fetch('http://localhost:8080/student/report', {
          credentials: 'include',
        });

        const data = await response.json();
        if (response.ok) {
          setReport(data);
          setErrorMessage('');
        } else {
          setErrorMessage(data.message);
          setReport([])
        }

      } catch (error) {
        setErrorMessage(error.message);
      }
    };

  return (
      <div>
        <h2 className="actionName">View Enrollment Report</h2>
        <button className="actionButton" onClick={fetchReport}>View Report</button>

        {errorMessage && <div className="errorMessage">{errorMessage}</div>}

        {report.length > 0 && (
            <table className="table">
              <thead>
              <tr>
                <th>Course</th>
                <th>Grade</th>
                <th>Status</th>
                <th>Teacher Name</th>
              </tr>
              </thead>
              <tbody>
              {report.map((item, index) => (
                  <tr key={index}>
                    <td>{item.course}</td>
                    <td>{item.grade}</td>
                    <td>{item.status}</td>
                    <td>{item.teacherName}</td>
                  </tr>
              ))}
              </tbody>
            </table>
        )}
      </div>
  );
}

function StudentActions() {
  return (
      <div>
        <Dashboard />
        <ListParallelsForCourseNextSemester/>
        <ViewEnrollmentReport/>
      </div>
  );
}


function ListMyCourses() {
  const [courses, setCourses] = useState([]);
  const [errorMessage, setErrorMessage] = useState('');

  const fetchCourses = async () => {
    try {
      const response = await fetch('http://localhost:8080/teacher/course', {
        credentials: 'include',
      });
      if (!response.ok) {
        throw new Error('Failed to fetch courses.');
      }
      const data = await response.json();

      setCourses(data);
      setErrorMessage('');
    } catch (error) {
      console.error('Error fetching courses:', error);
      setErrorMessage('Failed to fetch courses.');
    }
  };

  return (
      <div>
        <h2 className={"actionName"}>List My Courses</h2>
        <button className={"actionButton"} onClick={fetchCourses}>List Courses</button>

        {errorMessage && <div className={"errorMessage"}>{errorMessage}</div>}

        {courses.length > 0 && (
            <table className="table">
              <thead>
              <tr>
                <th>Course Code</th>
                <th>Course Name</th>
                <th>ECTS</th>
                <th>Parallels</th>
              </tr>
              </thead>
              <tbody>
              {courses.map((item, index) => (
                  <tr key={index}>
                    <td>{item.code}</td>
                    <td>{item.name}</td>
                    <td>{item.ects}</td>
                    <td>{item.parallelsList.join(" ")}</td>
                  </tr>
              ))}
              </tbody>
            </table>
        )}
      </div>
  );
}

function ListStudentsForParallel() {
  const [parallelId, setParallelId] = useState('');
  const [students, setStudents] = useState([]);
  const [errorMessage, setErrorMessage] = useState('');

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      const response = await fetch(`http://localhost:8080/teacher/students/${parallelId}`, {
        credentials: 'include', // Include cookies
      });
      if (!response.ok) {
        throw new Error('Failed to fetch students.');
      }
      const data = await response.json();
      setStudents(data);
      setErrorMessage(''); // Clear the error message on successful fetch
    } catch (error) {
      setErrorMessage(`Failed to fetch students.`);
      setStudents([]); // Clear the students data on error
    }
  };

  return (
      <div>
        <h2 className={"actionName"}>List Students For Parallel</h2>
        <form onSubmit={handleSubmit}>
          <label htmlFor="parallelId">Parallel ID:</label><br/>
          <input type="text" id="parallelId" value={parallelId} onChange={(e) => setParallelId(e.target.value)}/><br/>
          <input type="submit" value="List Students"/>
        </form>

        {errorMessage && <div className={"errorMessage"}>{errorMessage}</div>}

        {students.length > 0 && (
        <table className="table">
          <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Username</th>
          </tr>
          </thead>
          <tbody>
          {students.map((student, index) => (
              <tr key={index}>
                <td>{student.id}</td>
                <td>{student.firstName} {student.lastName}</td>
                <td>{student.userName}</td>
              </tr>
          ))}
          </tbody>
        </table>
        )}
      </div>
  );
}

function GradeStudent() {
  const [studentUsername, setStudentUsername] = useState('');
  const [grade, setGrade] = useState('');
  const [course, setCourse] = useState('');
  const [teacherName, setTeacherName] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const handleSubmit = async (event) => {
    event.preventDefault();

    const data = {
      course,
      teacherName,
      grade
    };

    try {
      const response = await fetch(`http://localhost:8080/teacher/grade/${studentUsername}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
        credentials: 'include', // Include cookies
      });
      if (!response.ok) {
        throw new Error(`Failed to grade student`);
      }
      setSuccessMessage('Student graded successfully.');
      setErrorMessage(''); // Clear the error message on successful grading
    } catch (error) {
      console.error('Error grading student:', error);
      setErrorMessage(`Failed to grade student`);
    }
  };
  return (
      <div>
        <h2 className={"actionName"}>Grade Student</h2>
        <form onSubmit={handleSubmit}>
          <label htmlFor="studentUsername">Student Username:</label><br />
          <input type="text" id="studentUsername" value={studentUsername} onChange={(e) => setStudentUsername(e.target.value)} /><br />
          <label htmlFor="grade">Grade:</label><br />
          <input type="text" id="grade" value={grade} onChange={(e) => setGrade(e.target.value)} /><br />
          <input type="submit" value="Grade Student" />
        </form>

        {successMessage && <div className={"successMessage"}>{successMessage}</div>}
        {errorMessage && <div sclassName={"errorMessage"}>{errorMessage}</div>}
      </div>
  );
}

function TeacherActions() {
  return (
      <div>
        <ListMyCourses/>
        <ListStudentsForParallel/>
        <GradeStudent/>
      </div>
  );
}


function AdminActions() {
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const [activeSemester, setActiveSemester] = useState(null);

  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [code, setCode] = useState('');
  const [isActive, setIsActive] = useState(false);
  const [semesterType, setSemesterType] = useState('SPRING');
  const [activeSemesterCode, setActiveSemesterCode] = useState('');
  const [semesters, setSemesters] = useState([]);

  const [year, setYear] = useState(new Date().getFullYear());

  function CreateSemester() {
    const [year, setYear] = useState(new Date().getFullYear());
    const [semesterType, setSemesterType] = useState('SPRING');
    const [message, setMessage] = useState('');

    const handleSubmit = async (e) => {
      e.preventDefault();

      const data = {
        year,
        semesterType
      };

      try {
        const response = await fetch('http://localhost:8080/rest/admin/semester', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(data),
          credentials: 'include', // Include cookies in the request
        });

        if (!response.ok) {
          throw new Error(`Semester creation failed with status: ${response.status}`);
        }

        setMessage('Semester created successfully.');
      } catch (error) {
        setMessage(`Failed to create semester: ${error.message}`);
      }
    };

    return (
        <div>
          <form onSubmit={handleSubmit}>
            <label>
              Year:
              <input type="number" value={year} onChange={(e) => setYear(e.target.value)} />
            </label>
            <label>
              Semester Type:
              <select value={semesterType} onChange={(e) => setSemesterType(e.target.value)}>
                <option value="SPRING">Spring</option>
                <option value="FALL">Fall</option>
              </select>
            </label>
            <button type="submit">Create Semester</button>
          </form>
          {message && <p>{message}</p>}
        </div>
    );
  }

  function SetActiveSemesterFunction() {
    const [semesterCode, setSemesterCode] = useState('');
    const [message, setMessage] = useState('');

    const handleSubmit = async (e) => {
      e.preventDefault(); // This line prevents the page from refreshing

      try {
        const response = await fetch(`http://localhost:8080/rest/admin/semester/${semesterCode}`, {
          method: 'PATCH',
          credentials: 'include', // Include cookies in the request
        });

        if (!response.ok) {
          throw new Error(`${response.status}`);
        }

        setMessage('Active semester set successfully.');
      } catch (error) {
        setMessage(`Failed to set active semester: ${error.message}`);
      }
    };

    return (
        <div>
          <form onSubmit={handleSubmit}>
            <label>
              Semester Code:
              <input type="text" value={semesterCode} onChange={(e) => setSemesterCode(e.target.value)} />
            </label>
            <button type="submit">Set Active Semester</button>
          </form>
          {message && <p>{message}</p>}
        </div>
    );
  }

  const handleGetAllSemesters = async () => {
    try {
      const response = await fetch('http://localhost:8080/rest/admin/semester/all', {
        credentials: 'include', // Include cookies
      });
      if (!response.ok) {
        throw new Error(`Failed to fetch all semesters with status: ${response.status}`);
      }
      const responseData = await response.json();
      setSemesters(responseData);
    } catch (error) {
      console.error(error);
      setErrorMessage(`Failed to fetch all semesters`);
    }
  };

  function GetActiveSemester() {
    const [semesterCode, setSemesterCode] = useState('');
    const [message, setMessage] = useState('');

    const handleGetActiveSemester = async () => {
      try {
        const response = await fetch('http://localhost:8080/rest/admin/semester/active', {
          credentials: 'include', // Include cookies
        });
        if (!response.ok) {
          throw new Error(`Failed to fetch active semester with status: ${response.status}`);
        }
        const data = await response.json();
        setSemesterCode(data.code);
        setMessage('Active semester fetched successfully.');
      } catch (error) {
        console.error('Error fetching active semester:', error);
        setMessage(`${error.message}`);
        setSemesterCode(''); // Clear the active semester code on error
      }
    };

    return (
        <div>
          <button className="actionButton" onClick={handleGetActiveSemester}>Get Active Semester</button>
          {message && <p>{message}</p>}
          {semesterCode && <p>Active Semester Code: {semesterCode}</p>}
        </div>
    );
  }

   return (
      <div>
        <h2 className={"actionName"}>Create Semester</h2>
        <CreateSemester/>

        <h2 className={"actionName"}>Set Active Semester by code</h2>
        <SetActiveSemesterFunction/>

        <h2 className={"actionName"}>Get All Semesters</h2>
        <button className="actionButton" onClick={handleGetAllSemesters}>Get Semesters</button>
        {semesters.map((semester) => (
            <p key={semester.id}>{semester.code}</p>
        ))}

        <h2 className={"actionName"}>Get Active Semester</h2>
        <GetActiveSemester/>
      </div>
  );
}

function App() {
  const [roleSelected, setRoleSelected] = useState('');
  const [showRegisterForm, setShowRegisterForm] = useState(false);
  const [showLoginForm, setShowLoginForm] = useState(false);
  const [showStudentActions, setShowStudentActions] = useState(false);
  const [showTeacherActions, setShowTeacherActions] = useState(false);
  const [showAdminActions, setShowAdminActions] = useState(false);

  const hideAll = () => {
    setShowRegisterForm(false);
    setShowLoginForm(false);
    setShowStudentActions(false);
    setShowTeacherActions(false);
    setShowAdminActions(false);
  }

  const toggleRegisterForm = () => {
    hideAll();
    setShowRegisterForm(!showRegisterForm);
  };

  const toggleLoginForm = () => {
    hideAll();
    setShowLoginForm(!showLoginForm);
  };

  const toggleStudentActions = () => {
    hideAll();
    setShowStudentActions(!showStudentActions);
  };

  const toggleTeacherActions = () => {
    hideAll();
    setShowTeacherActions(!showTeacherActions);
  };

  const toggleAdminActions = () => {
    hideAll();
    setShowAdminActions(!showAdminActions);
  };

  return (
      <div>
        <nav className="menu">
          <ul>
            <li onClick={() => { setRoleSelected('student'); toggleStudentActions(); }}>
              <a href="#">Student Actions</a>
            </li>
            <li onClick={() => { setRoleSelected('teacher'); toggleTeacherActions(); }}>
              <a href="#">Teacher Actions</a>
            </li>
            <li onClick={() => { setRoleSelected('admin'); toggleAdminActions(); }}>
              <a href="#">Admin Actions</a>
            </li>
            <li onClick={toggleLoginForm}>
              <a href="#">Login</a>
            </li>
            <li onClick={toggleRegisterForm}>
              <a href="#">Register</a>
            </li>
          </ul>
        </nav>

        <h1 className="name">School Information System</h1>

        {showRegisterForm && <RegisterForm/>}
        {showLoginForm && <LoginForm/>}

        {roleSelected === 'student' && showStudentActions && <StudentActions/>}
        {roleSelected === 'teacher' && showTeacherActions && <TeacherActions/>}
        {roleSelected === 'admin' && showAdminActions && <AdminActions/>}

      </div>
  );
}


export default App;
