<?php
require_once __DIR__ . '/config.php';

class API {
    function home($input){

        $email = $this->prepareData($input);

        $db = new Connect;
        $attractions = array();
        $data = $db->prepare("SELECT a.*, r.star as rating, 
        CASE
        WHEN f.email IS NOT NULL THEN 1
        ELSE 0
        END AS bookmark
        FROM
        (SELECT a.*, SUM(r.star)/COUNT(r.star) AS `avgRating`, COUNT(r.star) AS `ratingCount`
        FROM `place` AS a
        LEFT JOIN `rating` AS r ON a.pid = r.pid
        GROUP BY a.pid) 
        AS a
        LEFT JOIN `rating` AS r ON a.pid = r.pid 
        AND r.email = :a
        LEFT JOIN `favourite` AS f ON a.pid = f.pid
        AND f.email = :b
        WHERE a.category = 1
        ORDER BY a.`avgRating` DESC LIMIT 10");

        $data->bindParam(':a',$email);
        $data->bindParam(':b',$email);

        $data->execute();
        while($OutputData = $data->fetch(PDO::FETCH_ASSOC)){
            $temp = array(
                'pid'           => $OutputData['pid'],
                'address'       => $OutputData['address'],
                'name'          => $OutputData['name'],
                'city'          => $OutputData['city'],
                'state'         => $OutputData['state'],
                'phone'         => $OutputData['phone'],
                'latitude'      => $OutputData['latitude'],
                'longitude'     => $OutputData['longitude'],
                'description'   => $OutputData['description'],
                'placeid'       => $OutputData['placeid'],
                'openHour'      => $OutputData['openHour'],
                'closeHour'     => $OutputData['closeHour'],
                'avgRating'     => $OutputData['avgRating'],
                'ratingCount'   => $OutputData['ratingCount'],
                'imageLink'     => $OutputData['imageLink'],
                'email'         => $OutputData['email'],
                'category'      => $OutputData['category'],
                'rating'        => $OutputData['rating'],
                'bookmark'      => $OutputData['bookmark']
            );
            array_push($attractions,$temp);
        }
        return json_encode($attractions);
    }

    function search($keyword, $email){
        $email = $this->prepareData($email);
        $keyword = (string)"%". $keyword . "%";

        $db = new Connect;
        $results = array();
        $data = $db->prepare("SELECT a.*, r.star AS rating, 
        CASE
        WHEN f.email IS NOT NULL THEN 1
        ELSE 0
        END AS bookmark
        FROM
        (SELECT a.*, SUM(r.star)/COUNT(r.star) AS `avgRating`, COUNT(r.star) AS `ratingCount`
        FROM `place` AS a
        LEFT JOIN `rating` AS r ON a.pid = r.pid
        GROUP BY a.pid) 
        AS a
        LEFT JOIN `rating` AS r ON a.pid = r.pid 
        AND r.email = :d
        LEFT JOIN `favourite` AS f ON a.pid = f.pid
        AND f.email = :e
        WHERE a.city LIKE :a OR
        a.state LIKE :b OR
        a.name LIKE :c
        ORDER BY a.pid ASC");

        //get attraction
        $data->bindParam(':a',$keyHolder);
        $data->bindParam(':b',$keyHolder);
        $data->bindParam(':c',$keyHolder);
        $data->bindParam(':d',$email);
        $data->bindParam(':e',$email);

        $keyHolder = $keyword;
        $data->execute();
        while($OutputData = $data->fetch(PDO::FETCH_ASSOC)){
            $temp = array(
                'pid'           => $OutputData['pid'],
                'address'       => $OutputData['address'],
                'name'          => $OutputData['name'],
                'city'          => $OutputData['city'],
                'state'         => $OutputData['state'],
                'phone'         => $OutputData['phone'],
                'latitude'      => $OutputData['latitude'],
                'longitude'     => $OutputData['longitude'],
                'description'   => $OutputData['description'],
                'placeid'       => $OutputData['placeid'],
                'openHour'      => $OutputData['openHour'],
                'closeHour'     => $OutputData['closeHour'],
                'avgRating'     => $OutputData['avgRating'],
                'ratingCount'   => $OutputData['ratingCount'],
                'imageLink'     => $OutputData['imageLink'],
                'email'         => $OutputData['email'],
                'category'      => $OutputData['category'],
                'rating'        => $OutputData['rating'],
                'bookmark'      => $OutputData['bookmark']
            );
            
            array_push($results,$temp);  
            
        }

        return json_encode($results);
    }

    function keySearch($keyword, $email){
        $email = $this->prepareData($email);

        $db = new Connect;
        $data = $db->prepare("SELECT a.*, r.star AS rating, 
        CASE
        WHEN f.email IS NOT NULL THEN 1
        ELSE 0
        END AS bookmark
        FROM
        (SELECT a.*, SUM(r.star)/COUNT(r.star) AS `avgRating`, COUNT(r.star) AS `ratingCount`
        FROM `place` AS a
        LEFT JOIN `rating` AS r ON a.pid = r.pid
        GROUP BY a.pid) 
        AS a
        LEFT JOIN `rating` AS r ON a.pid = r.pid 
        AND r.email = :d
        LEFT JOIN `favourite` AS f ON a.pid = f.pid
        AND f.email = :e
        WHERE a.city = :a OR
        a.state = :b OR
        a.name = :c
        ORDER BY a.pid ASC");

        //get attraction
        $data->bindParam(':a',$keyHolder);
        $data->bindParam(':b',$keyHolder);
        $data->bindParam(':c',$keyHolder);
        $data->bindParam(':d',$email);
        $data->bindParam(':e',$email);

        $keyHolder = $keyword;
        $data->execute();
        while($OutputData = $data->fetch(PDO::FETCH_ASSOC)){
            $result = array(
                'pid'           => $OutputData['pid'],
                'address'       => $OutputData['address'],
                'name'          => $OutputData['name'],
                'city'          => $OutputData['city'],
                'state'         => $OutputData['state'],
                'phone'         => $OutputData['phone'],
                'latitude'      => $OutputData['latitude'],
                'longitude'     => $OutputData['longitude'],
                'description'   => $OutputData['description'],
                'placeid'       => $OutputData['placeid'],
                'openHour'      => $OutputData['openHour'],
                'closeHour'     => $OutputData['closeHour'],
                'avgRating'     => $OutputData['avgRating'],
                'ratingCount'   => $OutputData['ratingCount'],
                'imageLink'     => $OutputData['imageLink'],
                'email'         => $OutputData['email'],
                'category'      => $OutputData['category'],
                'rating'        => $OutputData['rating'],
                'bookmark'      => $OutputData['bookmark']
            );
            
        }

        return json_encode($result);
    }

    function nearby($latitude, $longitude, $cat, $email){

        $email = $this->prepareData($email);
        $db = new Connect;
        $results = array();
        $data = $db->prepare("SELECT a.*, r.star AS rating, 
        CASE
        WHEN f.email IS NOT NULL THEN 1
        ELSE 0
        END AS bookmark
        FROM
        (SELECT
        a.*,
        SUM(r.star)/COUNT(r.star) AS `avgRating`, COUNT(r.star) AS `ratingCount`,
        ACOS( SIN( RADIANS( a.latitude ) ) * SIN( RADIANS( :a ) ) + COS( RADIANS( a.latitude ) )
        * COS( RADIANS( :b )) * COS( RADIANS( a.longitude ) - RADIANS( :c )) ) * 6380 AS `distance`
        FROM place AS a
        LEFT JOIN rating AS r ON a.pid = r.pid
        WHERE
        ACOS( SIN( RADIANS( a.latitude ) ) * SIN( RADIANS( :d ) ) + COS( RADIANS( a.latitude ) )
        * COS( RADIANS( :e )) * COS( RADIANS( a.longitude  ) - RADIANS( :f )) ) * 6380 < 10
        GROUP BY a.pid
        ) 
        AS a
        LEFT JOIN `rating` AS r ON a.pid = r.pid 
        AND r.email = :h
        LEFT JOIN `favourite` AS f ON a.pid = f.pid
        AND f.email = :i
        WHERE a.category = :g
        ORDER BY `distance`");

        //get attraction
        $data->bindParam(':a',$lat);
        $data->bindParam(':b',$lat);
        $data->bindParam(':c',$long);
        $data->bindParam(':d',$lat);
        $data->bindParam(':e',$lat);
        $data->bindParam(':f',$long);
        $data->bindParam(':g',$category);
        $data->bindParam(':h',$email);
        $data->bindParam(':i',$email);

        $lat = $latitude;
        $long = $longitude;
        $category = $cat;

        $data->execute();
        while($OutputData = $data->fetch(PDO::FETCH_ASSOC)){
            $temp = array(
                'pid'           => $OutputData['pid'],
                'address'       => $OutputData['address'],
                'name'          => $OutputData['name'],
                'city'          => $OutputData['city'],
                'state'         => $OutputData['state'],
                'phone'         => $OutputData['phone'],
                'latitude'      => $OutputData['latitude'],
                'longitude'     => $OutputData['longitude'],
                'description'   => $OutputData['description'],
                'placeid'       => $OutputData['placeid'],
                'openHour'      => $OutputData['openHour'],
                'closeHour'     => $OutputData['closeHour'],
                'avgRating'     => $OutputData['avgRating'],
                'ratingCount'   => $OutputData['ratingCount'],
                'imageLink'     => $OutputData['imageLink'],
                'email'         => $OutputData['email'],
                'category'      => $OutputData['category'],
                'rating'        => $OutputData['rating'],
                'bookmark'      => $OutputData['bookmark']
            );
            
            array_push($results,$temp);  
            
        }

        return json_encode($results);
    }

    function allNearby($latitude, $longitude, $email){
        $email = $this->prepareData($email);
        $db = new Connect;
        $results = array();
        $data = $db->prepare("SELECT a.*, r.star AS rating, 
        CASE
        WHEN f.email IS NOT NULL THEN 1
        ELSE 0
        END AS bookmark
        FROM
        (SELECT
        a.*,
        SUM(r.star)/COUNT(r.star) AS `avgRating`, COUNT(r.star) AS `ratingCount`,
        ACOS( SIN( RADIANS( a.latitude ) ) * SIN( RADIANS( :a ) ) + COS( RADIANS( a.latitude ) )
        * COS( RADIANS( :b )) * COS( RADIANS( a.longitude ) - RADIANS( :c )) ) * 6380 AS `distance`
        FROM place AS a
        LEFT JOIN rating AS r ON a.pid = r.pid
        WHERE
        ACOS( SIN( RADIANS( a.latitude ) ) * SIN( RADIANS( :d ) ) + COS( RADIANS( a.latitude ) )
        * COS( RADIANS( :e )) * COS( RADIANS( a.longitude  ) - RADIANS( :f )) ) * 6380 < 10 
        GROUP BY a.pid
        ) 
        AS a
        LEFT JOIN `rating` AS r ON a.pid = r.pid 
        AND r.email = :g
        LEFT JOIN `favourite` AS f ON a.pid = f.pid
        AND f.email = :h
        ORDER BY `distance`");

        //get attraction
        $data->bindParam(':a',$lat);
        $data->bindParam(':b',$lat);
        $data->bindParam(':c',$long);
        $data->bindParam(':d',$lat);
        $data->bindParam(':e',$lat);
        $data->bindParam(':f',$long);
        $data->bindParam(':g',$email);
        $data->bindParam(':h',$email);

        $lat = $latitude;
        $long = $longitude;

        $data->execute();
        while($OutputData = $data->fetch(PDO::FETCH_ASSOC)){
            $temp = array(
                'pid'           => $OutputData['pid'],
                'address'       => $OutputData['address'],
                'name'          => $OutputData['name'],
                'city'          => $OutputData['city'],
                'state'         => $OutputData['state'],
                'phone'         => $OutputData['phone'],
                'latitude'      => $OutputData['latitude'],
                'longitude'     => $OutputData['longitude'],
                'description'   => $OutputData['description'],
                'placeid'       => $OutputData['placeid'],
                'openHour'      => $OutputData['openHour'],
                'closeHour'     => $OutputData['closeHour'],
                'avgRating'     => $OutputData['avgRating'],
                'ratingCount'   => $OutputData['ratingCount'],
                'imageLink'     => $OutputData['imageLink'],
                'email'         => $OutputData['email'],
                'category'      => $OutputData['category'],
                'rating'        => $OutputData['rating'],
                'bookmark'      => $OutputData['bookmark']
            );
            
            array_push($results,$temp);  
            
        }

        return json_encode($results);
    }

    function signIn($remail, $rpassword){

        $email = $this->prepareData($remail);
        $password = $this->prepareData($rpassword);

        $db = new Connect;
        $data = $db->prepare('SELECT *
        FROM `user`
        WHERE `email` = :a');

        //get attraction
        $data->bindParam(':a',$email);

        $data->execute();
        while($OutputData = $data->fetch(PDO::FETCH_ASSOC)){
            if(!is_null($OutputData)){
                if ($email == $OutputData['email'] && password_verify($password, $OutputData['password'])) {
                    $result = array(
                        'email'         => $OutputData['email'],
                        'name'          => $OutputData['name'],
                        'password'      => $rpassword,
                        'dob'           => $OutputData['dob'],
                        'phone'         => $OutputData['phone']
                    );
                }else{
                    $result = array();
                }
            }else{
                $result = array();
            }
            
        }

        return json_encode($result);
    }

    function signUp($input){  
        $name       = $this->prepareData($input->name);
        $dob        = $this->prepareData($input->dob);
        $phone      = $this->prepareData($input->phone);
        $password   = $this->prepareData($input->password);
        $email      = $this->prepareData($input->email);
        $hash       = password_hash($password, PASSWORD_DEFAULT);

        $db = new Connect;
        $data = $db->prepare(
        "INSERT INTO `user` (`email`, `password`, `name`, `dob`, `phone`) 
        VALUES ('" . $email . "','" . $hash . "','" . $name . "','" . $dob . "','" . $phone . "')");

        if($data->execute()){
            $response = array(
                'email'    => $input->email,
                'name'     => $input->name,
                'password' => $input->password,
                'dob'      => $input->dob,
                'phone'    => $input->phone
            );
        }else{
            $response = array();
        }
        
        return json_encode($response);

    }

    function changePw($input){
        $password   = $this->prepareData($input->password);
        $email      = $this->prepareData($input->email);
        $hash       = password_hash($password, PASSWORD_DEFAULT);

        $db = new Connect;
        $data = $db->prepare(
        "UPDATE `user`
        SET `password` = '".$hash ."'
        WHERE `email` = '".$email."'"); 
        

        if($data->execute()){
            $response = array(
                'email'    => $input->email,
                'name'     => $input->name,
                'password' => $input->password,
                'dob'      => $input->dob,
                'phone'    => $input->phone
            );
        }else{
            $response = array();
        }
        
        return json_encode($response);
    }

    function updateProfile($input){
        $email      = $this->prepareData($input->email);
        $name       = $this->prepareData($input->name);
        $dob        = $this->prepareData($input->dob);
        $phone      = $this->prepareData($input->phone);

        $db = new Connect;
        $data = $db->prepare(
        "UPDATE `user`
        SET `name` = '" . $name . "',`dob` = '" . $dob . "',`phone` = '" . $phone . "'
        WHERE `email` = '".$email."'"); 
        

        if($data->execute()){
            $response = array(
                'email'    => $input->email,
                'name'     => $input->name,
                'password' => $input->password,
                'dob'      => $input->dob,
                'phone'    => $input->phone
            );
        }else{
            $response = array();
        }
        
        return json_encode($response);
    }

    function getBookmark($input){
        $email = $this->prepareData($input);

        $db = new Connect;
        $bookmarks = array();
        $data = $db->prepare(
        "SELECT a.*, r.star AS rating, 
        CASE
        WHEN f.email IS NOT NULL THEN 1
        ELSE 0
        END AS bookmark
        FROM
        (SELECT p.*, SUM(r.star)/COUNT(r.star) AS `avgRating`, COUNT(r.star) AS `ratingCount`
        FROM `place` p
        RIGHT JOIN `favourite` f ON p.pid = f.pid
        RIGHT JOIN `user` u ON f.email = u.email
        LEFT JOIN `rating` r ON r.pid = p.pid
        WHERE u.email = :a
        GROUP BY p.pid)
        AS a
        LEFT JOIN `rating` AS r ON a.pid = r.pid 
        AND r.email = :b
        LEFT JOIN `favourite` AS f ON a.pid = f.pid
        AND f.email = :c
        ORDER BY a.pid DESC");

        //get attraction
        $data->bindParam(':a',$email);
        $data->bindParam(':b',$email);
        $data->bindParam(':c',$email);

        $data->execute();
        while($OutputData = $data->fetch(PDO::FETCH_ASSOC)){
            $temp = array(
                'pid'           => $OutputData['pid'],
                'address'       => $OutputData['address'],
                'name'          => $OutputData['name'],
                'city'          => $OutputData['city'],
                'state'         => $OutputData['state'],
                'phone'         => $OutputData['phone'],
                'latitude'      => $OutputData['latitude'],
                'longitude'     => $OutputData['longitude'],
                'description'   => $OutputData['description'],
                'placeid'       => $OutputData['placeid'],
                'openHour'      => $OutputData['openHour'],
                'closeHour'     => $OutputData['closeHour'],
                'avgRating'     => $OutputData['avgRating'],
                'ratingCount'   => $OutputData['ratingCount'],
                'imageLink'     => $OutputData['imageLink'],
                'email'         => $OutputData['email'],
                'category'      => $OutputData['category'],
                'rating'        => $OutputData['rating'],
                'bookmark'      => $OutputData['bookmark']
            );
            array_push($bookmarks,$temp);
        }
        return json_encode($bookmarks);
    }

    function prepareData($data){
        return stripslashes(htmlspecialchars($data));
    }

    function addBookmark($pid, $email){  

        $db = new Connect;
        $data = $db->prepare(
        "INSERT INTO `favourite` (`email`, `pid`) 
        VALUES ('" . $email . "','" . $pid . "')");

        if($data->execute()){
            $response = 1;
        }else{
            $response = null;
        }
        
        return json_encode($response);

    }

    function removeBookmark($pid, $email){  

        $db = new Connect;
        $data = $db->prepare(
        "DELETE FROM `favourite` 
        WHERE `email` = '" . $email . "'
        AND `pid` = '" . $pid . "'");

        if($data->execute()){
            $response = 0;
        }else{
            $response = null;
        }
        
        return json_encode($response);

    }

    function submitRating($pid, $email, $star){  

        $db = new Connect;
        $data = $db->prepare(
        "INSERT INTO `rating` (`email`, `pid`, `star`) 
        VALUES ('" . $email . "','" . $pid . "','" . $star . "')");

        if($data->execute()){
            $response = (int)$star;
        }else{
            $response = null;
        }
        
        return json_encode($response);

    }

    function updateRating($pid, $email, $star){  

        $db = new Connect;
        $data = $db->prepare(
        "UPDATE `rating`
        SET `star` = '" . $star . "'
        WHERE `email`= '" . $email . "' AND `pid` = " . $pid . "");

        if($data->execute()){
            $response = (int)$star;
        }else{
            $response = null;
        }
        
        return json_encode($response);

    }



}

?>