<?php
error_reporting(E_ALL);

require_once 'include/DB_Functions.php';

$db = new DB_Functions();

        $json_score_all = $db->showScore();

echo json_encode($json_score_all);

?>