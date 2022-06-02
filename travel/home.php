<?php
require_once  __DIR__ . '/API.php';

$API = new API;
header('Content-Type: application/json');

if(isset($_GET['email']))
    echo $API->home($_GET['email']);
elseif(!isset($_GET['email']))
    echo $API->home("");    
else
    die();
?>