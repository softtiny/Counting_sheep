import json
import datetime

with open("./old-update.json") as old:
    data = json.load(old)

data['latestVersion']="1.0." + datetime.datetime.strftime(datetime.datetime.now(),'%Y%m%d-%H%M%S')
data['latestVersionCode'] +=1
data['url']='https://github.com/softtiny/Counting_sheep/releases/latest/download/' + data['latestVersion'] + ".apk"

with open("./update.json","w+") as update:
    json.dump(data,update)