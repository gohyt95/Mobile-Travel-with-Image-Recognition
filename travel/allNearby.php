<?php
require_once  __DIR__ . '/API.php';

//192.168.0.104/travel/

header('Content-Type: application/json');

$API = new API;

//echo $_GET['keyword'];

if(isset($_GET['lat']) && isset($_GET['long']) && isset($_GET['email']))
    echo $API->allNearby($_GET['lat'], $_GET['long'], $_GET['email']);
elseif(isset($_GET['lat']) && isset($_GET['long']) && !isset($_GET['email']))
    echo $API->allNearby($_GET['lat'], $_GET['long'], "");
else
    die();

?>