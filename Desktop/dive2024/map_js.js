// 메뉴바 열기 및 닫기
function toggleMenu() {
    const menuItems = document.getElementById('menuItems');
    menuItems.style.display = menuItems.style.display === 'block' ? 'none' : 'block';
}

// 지도 초기화
let map;
let markers = {};

// 마커 정보
const markerData = {
    'tour': [{ lat: 35.159545, lng: 129.160667, title: 'Tour Spot' }],
    'event': [{ lat: 35.153665, lng: 129.157400, title: 'Event Spot' }],
    'historic': [{ lat: 35.151233, lng: 129.162344, title: 'Historic Spot' }],
    'point': [{ lat: 35.156123, lng: 129.165567, title: 'Point Spot' }]
};

// 구글 맵 로드
// 지도 초기화 함수
function initMap() {
    const centerPosition = { lat: 35.159545, lng: 129.160667 }; // 기본 중앙 위치

    // 기본 지도 UI 컨트롤을 모두 숨김
    map = new google.maps.Map(document.getElementById('map'), {
        center: centerPosition,
        zoom: 15,
        zoomControl: false, // 확대/축소 버튼 숨기기
        mapTypeControl: false, // 지도/위성 전환 버튼 숨기기
        fullscreenControl: false, // 전체 화면 버튼 숨기기
        streetViewControl: false, // 스트리트 뷰 버튼 숨기기
    });
}

// 페이지 로드 시 initMap 함수 호출
window.onload = initMap;


// 페이지 로드 시 initMap 함수 호출
window.onload = initMap;


// 마커 토글 함수
function toggleMarkers(type) {
    if (!markers[type]) {
        markers[type] = markerData[type].map(data => {
            return new google.maps.Marker({
                position: { lat: data.lat, lng: data.lng },
                map: map,
                title: data.title
            });
        });
    } else {
        markers[type].forEach(marker => {
            marker.setMap(marker.getMap() ? null : map);
        });
    }
}

// 메뉴바 열기 및 닫기
function toggleMenu() {
    const menuItems = document.getElementById('menuItems');
    menuItems.style.display = menuItems.style.display === 'block' ? 'none' : 'block';
}


window.onload = initMap;
