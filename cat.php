<?php
error_reporting(E_ALL);

require_once 'include/DB_Functions.php';

$db = new DB_Functions();

        $json_cat_all = $db->showKat();

echo json_encode($json_cat_all);

?>