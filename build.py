import json
import datetime
import sys

with open("./old-update.json") as old:
    data = json.load(old)

diff = 1
args = sys.argv
if len(args) > 1:
    val = args[-1]
    diff = int(val)

data['latestVersion']="1.0." + datetime.datetime.strftime(datetime.datetime.now(),'%Y%m%d-%H%M%S')
data['latestVersionCode'] +=diff
if data['latestVersionCode'] <=0:
    data['latestVersionCode'] = 1
data['url']='https://github.com/softtiny/Counting_sheep/releases/latest/download/' + data['latestVersion'] + ".apk"

with open("./update.json","w+") as update:
    json.dump(data,update)