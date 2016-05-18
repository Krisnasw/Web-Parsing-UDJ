<?php
error_reporting(E_ALL);

require_once 'include/DB_Functions.php';

$db = new DB_Functions();

        $nama = $_POST["nama"];

        $json_score_all = $db->showScore($nama);

echo json_encode($json_score_all);

?>