<?php
require('../vendor/autoload.php');

$app = new Silex\Application();
$app['debug'] = true;

$app->register(new Silex\Provider\MonologServiceProvider(), array(
    'monolog.logfile' => 'php://stderr',
));

$app->register(new Silex\Provider\TwigServiceProvider(), array(
    'twig.path' => __DIR__.'/views',
));

$app->post('/write', function() use ($app) {
    if (!isset($_REQUEST))
        return "";

    $text = file_get_contents('php://input');

    if($text != "" || $text != " "){
        $urlDB = parse_url(getenv("CLEARDB_DATABASE_URL")); //Подключаемся к бд

        $server = $urlDB["host"];
        $username = $urlDB["user"];
        $password = $urlDB["pass"];
        $db = substr($urlDB["path"], 1);

        $mysqli = new mysqli($server, $username, $password, $db);

        if ($mysqli->connect_error) {//проверка подключились ли мы
            die('Ошибка подключения (' . $mysqli->connect_errno . ') ' . $mysqli->connect_error); //если нет выводим ошибку и выходим из кода
        } else {

            $mysqli->query("SET NAMES 'utf8'");
            $mysqli->query("CREATE TABLE IF NOT EXISTS  `logs`(`id` Int( 255 ) AUTO_INCREMENT NOT NULL, `log` Text NOT NULL, CONSTRAINT `unique_id` UNIQUE(`id`)) ENGINE = InnoDB;");
            $mysqli->query("INSERT INTO `logs` (`log`) VALUES ('" . $text . "')");
        }
    }

    return "ok";
});

$app->post('/getupdate', function() use ($app) {
    if (!isset($_REQUEST))
        return "";

    $text = file_get_contents('php://input');

    if($text != "" || $text != " "){
        $urlDB = parse_url(getenv("CLEARDB_DATABASE_URL")); //Подключаемся к бд

        $server = $urlDB["host"];
        $username = $urlDB["user"];
        $password = $urlDB["pass"];
        $db = substr($urlDB["path"], 1);

        $mysqli = new mysqli($server, $username, $password, $db);

        if ($mysqli->connect_error) {//проверка подключились ли мы
            die('Ошибка подключения (' . $mysqli->connect_errno . ') ' . $mysqli->connect_error); //если нет выводим ошибку и выходим из кода
        } else {
            $mysqli->query("SET NAMES 'utf8'");
            $res = $mysqli->query("SELECT * FROM `version`");
            $res = $res->$res->fetch_assoc();
            
        }
    }

    return "ok";
});

$app->get('/', function() use($app) {
    return "What are you doing here?<br>This is debugger!";
});

$app->run();
