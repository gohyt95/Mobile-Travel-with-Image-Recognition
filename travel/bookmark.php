<?php
require_once  __DIR__ . '/API.php';

//192.168.0.104/travel/

header('Content-Type: application/json');

$API = new API;

//echo $_GET['keyword'];

if(isset($_GET['email']))
    echo $API->getBookmark($_GET['email']);
else
    die();
?>