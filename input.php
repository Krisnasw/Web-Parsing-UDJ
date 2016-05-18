<?php

require_once 'include/DB_Functions.php';

$db = new DB_Functions();

       $nama = $_POST["nama"];
       $nilai = $_POST['nilai'];

$json_score = $db->storeScore($nama, $nilai);

echo json_encode($json_score);
    
?>