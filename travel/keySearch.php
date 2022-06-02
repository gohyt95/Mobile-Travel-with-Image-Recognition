<?php
require_once  __DIR__ . '/API.php';

//192.168.0.104/travel/

header('Content-Type: application/json');

$API = new API;

//echo $_GET['keyword'];

if(isset($_GET['keyword']) && isset($_GET['email']))
    echo $API->keySearch($_GET['keyword'],$_GET['email'] );
elseif(isset($_GET['keyword']) && !isset($_GET['email']))
    echo $API->keySearch($_GET['keyword'],"");
else
    die();

?>