<?php
require_once  __DIR__ . '/API.php';

//192.168.0.104/travel/

header('Content-Type: application/json');

$API = new API;

//echo $_GET['keyword'];

if(isset($_GET['lat']) && isset($_GET['long']) && isset($_GET['cat']) && isset($_GET['email']))
    echo $API->nearby($_GET['lat'], $_GET['long'], $_GET['cat'], $_GET['email']);
elseif(isset($_GET['lat']) && isset($_GET['long']) && isset($_GET['cat']) && !!isset($_GET['email']))
    echo $API->nearby($_GET['lat'], $_GET['long'], $_GET['cat'], "");   
else
    die();

?>