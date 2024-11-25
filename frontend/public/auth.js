// 사용자 인증 및 정보를 가져오는 함수
function getUserInfo() {
    return new Promise((resolve, reject) => {
      // Chrome Identity API를 사용하여 사용자 인증
      chrome.identity.getAuthToken({ interactive: true }, function(token) {
        if (chrome.runtime.lastError) {
          reject(chrome.runtime.lastError);
          return;
        }
  
        // 인증 토큰을 사용하여 사용자 정보 가져오기
        fetch('<https://www.googleapis.com/oauth2/v1/userinfo?alt=json>', {
          method: 'GET',
          headers: {
            'Authorization': 'Bearer ' + token
          }
        })
        .then(response => response.json())
        .then(userInfo => {
          const userEmail = userInfo.email;
          const userName = userInfo.name;
          resolve({ email: userEmail, name: userName });
        })
        .catch(error => {
          reject('사용자 정보를 가져오는 중 오류 발생: ' + error);
        });
      });
    });
  }
  