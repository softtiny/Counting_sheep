let Authorization = "Authorization: Bearer " + $env.GITHUB_TOKEN
# Fetch artifacts for the run
def get_artifacts () {
    let res = ( curl -L 
    -H "Accept: application/vnd.github+json"
    -H $Authorization
    "https://api.github.com/repos/softtiny/Counting_sheep/actions/runs?status=completed&conclusion=success&per_page=1")
    let data = $res | from json
    let workflow_runs = $data | get "workflow_runs"
    let workflow = $workflow_runs | get 0
    let run_id = $workflow | get id | into string
    print $run_id
    let url = "https://api.github.com/repos/softtiny/Counting_sheep/actions/runs/" + $run_id + "/artifacts";
    print $url
    let res = (
        curl -L
        -H "Accept: application/vnd.github+json"
        -H $Authorization
        $url
    )
    let data = $res | from json
    let artifacts = $data | get "artifacts"
    let artifact = $artifacts | get 0
    let archive_download_url = $artifact | get "archive_download_url"
    print $archive_download_url
    curl -v -L -H $Authorization $archive_download_url -o build/output.zip
    
}
def unzip () {
    ^$env.a7z x -obuild ./build/output.zip
}
def open_log () {
    start ./build/logcat.txt
    start ./build/log_test.txt
}

def git_up () {
    git add .
    try {
        git commit -m ":construction: Work in progress."
    } catch {
        print "nothing commit"
    }
    git push
}

def main () {
    git_up
    #get_artifacts
    #unzip
    #open_log
    echo "sadf"
}