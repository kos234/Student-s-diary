<?php
require('../vendor/autoload.php');
//https://git.heroku.com/students-diary.git

include "auch.php";

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
    }else

    return "ok";
});

$app->post('/conf', function() use ($app) {
    if (!isset($_REQUEST))
        return "";

    $data = json_decode(file_get_contents('php://input'));

    if (strcmp($data->secret, SECRET_KEY_VK_BOT) !== 0 && strcmp($data->type, 'confirmation') !== 0)
        return;//Если не наш, выдаем ошибку серверу vk

    if($data->type == "confirmation")
        echo CONFIRMATION_TOKEN_VK_BOT;
    else
        return "ok";
});

$app->get('/', function() use($app) {
    return "What are you doing here?<br>This is debugger!";
});

$app->run();
