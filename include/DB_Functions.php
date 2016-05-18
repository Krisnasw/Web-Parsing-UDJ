<?php

/**
 * @author Krisna Satria
 * 
 */

class DB_Functions {

    private $conn;

    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $db = new Db_Connect();
        $this->conn = $db->connect();
    }

    // destructor
    function __destruct() {
        
    }

    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($name, $email, $password) {
        $uuid = uniqid('', true);
        $hash = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"]; // salt

        $stmt = $this->conn->prepare("INSERT INTO users(unique_id, name, email, encrypted_password, salt, created_at) VALUES(?, ?, ?, ?, ?, NOW())");
        $stmt->bind_param("sssss", $uuid, $name, $email, $encrypted_password, $salt);
        $result = $stmt->execute();
        $stmt->close();

        // check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $user;
        } else {
            return false;
        }
    }

    /**
     * Get user by email and password
     */
    public function getUserByEmailAndPassword($email, $password) {

        $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");

        $stmt->bind_param("s", $email);

        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            // verifying user password
            $salt = $user['salt'];
            $encrypted_password = $user['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
            // check for password equality
            if ($encrypted_password == $hash) {
                // user authentication details are correct

                session_start();

                $_SESSION['uid'] = $user['unique_id'];
                $_SESSION['namalengkap'] = $user['name'];

                return $user;
            }
        } else {
            return NULL;
        }
    }

    /**
     * Check user is existed or not
     */
    public function isUserExisted($email) {
        $stmt = $this->conn->prepare("SELECT email from users WHERE email = ?");

        $stmt->bind_param("s", $email);

        $stmt->execute();

        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            // user existed 
            $stmt->close();
            return true;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
    }

    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {

        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }

    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {

        $hash = base64_encode(sha1($password . $salt, true) . $salt);

        return $hash;
    }

    public function getAllQuizQuestions () {

    $json_array = array();

    $q = "SELECT * FROM quiz_questions order by rand(id)";

    $result = $this->conn->query($q);

    if($result->num_rows > 0) {

        while ($row = mysqli_fetch_array($result)) {
            // $temp = array(
            //     'id' => $row['id'],
            //     'question' => $row['question'],
            //     'pila' => $row['pila'],
            //     'pilb' => $row['pilb'],
            //     'pilc' => $row['pilc'],
            //     'pild' => $row['pild'],
            //     'correct_answer' => $row['correct_answer'],
            //     'image' => "http://192.168.50.85/droid/images/".$row['image'].""
            // );
                // array_push($json_array, $temp);
            $json_array["quiz_questions"][] = $row;
        }
        
        // $data = json_encode($json_array);
 		// $data = str_replace("\\", "", $data);
 		// // echo "{\"daftar_soal\":" . $data . "}";

    }

    return $json_array;

    }

    public function storeScore ($nama, $nilai) {
         
       $query = "INSERT INTO hasil(id,nama,nilai) VALUES('','$nama','$nilai')";

       $hasil = $this->conn->query($query);

        if($hasil)
            {
            $response["success"] = "1";
            $response["message"] = "Data sukses diinput";
                echo json_encode($response);
            }
            else {
             $response["success"] = "0";
             $response["message"] = "Maaf , terjadi kesalahan";
         
                // echoing JSON response
                echo json_encode($response);
            }

    }

    public function showScore ($nama)
    {
        $json_array = array();

        $q = $this->conn->query("SELECT * FROM hasil WHERE nama = '$nama'");

        if ($q->num_rows > 0) {
            # code...
            while ($row = mysqli_fetch_array($q)) {
                # code...
                $json_array["hasil"][] = $row;
            }
        } else {
            $json_array["hasil"][] = "Kosong";
        }

        return $json_array;
    }

}

?>
