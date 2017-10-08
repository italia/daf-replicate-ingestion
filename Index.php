<?php
$url = 'http://opendata.5t.torino.it/get_fdt';
$xml = simplexml_load_string(get_web_page($url));

$data = array();
foreach($xml as $so){

	array_push($data, array(
		'lcd1' =>	(string)$so['lcd1'][0],
		'Road_LCD' => (string)$so['Road_LCD'][0],
		'Road_name' => (string)$so['Road_name'][0],
		'offset' => (string)$so['offset'][0],
		'direction' => (string)$so['direction'][0],
		'lat' => (string)$so['lat'][0],
		'lng' => (string)$so['lng'][0],
		'accuracy' => (string)$so['accuracy'][0],
		'period' => (string)$so['period'][0],
		'flow' => (string)$so->speedflow['flow'],
		'speed' => (string)$so->speedflow['speed']
	));
}
#echo json_decode($data);
#var_dump($so);

$result = array(
	'name' => 'Event',
   	'type' => 'record',
   	'doc' =>  'A generic event. See the reference guide for event format information.',
   	'version' => 3,
   	'fields' => $data
);


print json_encode($result);

#print($xml->FDT_data[1][‘lcd1’]);
#var_dump($xml);

function get_web_page($url) {
    $options = array(
        CURLOPT_RETURNTRANSFER => true,   // return web page
        CURLOPT_HEADER         => false,  // don’t return headers
        CURLOPT_FOLLOWLOCATION => true,   // follow redirects
        CURLOPT_MAXREDIRS      => 10,     // stop after 10 redirects
        CURLOPT_ENCODING       => “”,     // handle compressed
        CURLOPT_USERAGENT      => “test”, // name of client
        CURLOPT_AUTOREFERER    => true,   // set referrer on redirect
        CURLOPT_CONNECTTIMEOUT => 120,    // time-out on connect
        CURLOPT_TIMEOUT        => 120,    // time-out on response
    ); 

    $ch = curl_init($url);
    curl_setopt_array($ch, $options);

    $content  = curl_exec($ch);

    curl_close($ch);

    return $content;
}
