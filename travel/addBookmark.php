<?php
require_once  __DIR__ . '/API.php';

//192.168.0.104/travel/

header('Content-Type: application/x-www-form-urlencoded');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Access-Control-Allow-Headers, Content-Type,Access-Control-Allow-Methods,Authorization,X-Requested-With');

$API = new API;

if(isset($_POST['email']) && isset($_POST['pid']))
    echo $API->addBookmark($_POST['pid'],$_POST['email'] );
else
    die();
?>