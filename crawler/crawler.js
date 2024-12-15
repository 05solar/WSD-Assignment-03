const puppeteer = require('puppeteer');

const BASE_URL = 'https://www.saramin.co.kr/'; // 실제 URL로 변경
const proxyUrl = 'https://13.37.89.201:80'; // 필요 시 프록시 설정

async function autoScroll(page) {
    await page.evaluate(async () => {
        await new Promise((resolve) => {
            let totalHeight = 0;
            const distance = 100;
            const timer = setInterval(() => {
                const scrollHeight = document.body.scrollHeight;
                window.scrollBy(0, distance);
                totalHeight += distance;

                if (totalHeight >= scrollHeight) {
                    clearInterval(timer);
                    resolve();
                }
            }, 200);
        });
    });
}

async function crawlJobs() {
    // Puppeteer 런치 시 프록시 옵션 필요하다면 추가
    const browser = await puppeteer.launch({
        args: [
            `--proxy-server=${proxyUrl}`,
            '--no-sandbox',
            '--disable-setuid-sandbox'
        ],
        headless: true
    });

    const page = await browser.newPage();
    await page.setUserAgent('Mozilla/5.0 (Windows NT 10.0; Win64; x64)');

    // 페이지 접속
    await page.goto(BASE_URL, { waitUntil: 'networkidle2' });

    // 충분한 스크롤을 통해 데이터 로딩 유도
    for (let i = 0; i < 5; i++) {
        await autoScroll(page);
        await page.waitForTimeout(2000); // 데이터 로딩 대기
    }

    // 셀렉터에 맞추어 데이터 추출
    const jobItems = await page.$$eval('.item_recruit', items => {
        return items.map(el => {
            const titleElem = el.querySelector('.job_tit a');
            const companyElem = el.querySelector('.corp_name a');
            const conditions = el.querySelectorAll('.job_condition span');
            const deadlineElem = el.querySelector('.job_date .date');
            const sectorElem = el.querySelector('.job_sector');
            const postedDateElem = el.querySelector('span.job_day');

            const title = titleElem ? titleElem.textContent.trim() : 'No title provided';
            const company = companyElem ? companyElem.textContent.trim() : 'No company provided';
            const link = titleElem ? (titleElem.href.startsWith('http') ? titleElem.href : `https://www.saramin.co.kr${titleElem.getAttribute('href')}`) : 'No link provided';
            const location = conditions[0] ? conditions[0].textContent.trim() : 'No location provided';
            const experience = conditions[1] ? conditions[1].textContent.trim() : 'No experience provided';
            const education = conditions[2] ? conditions[2].textContent.trim() : 'No education provided';
            const employmentType = conditions[3] ? conditions[3].textContent.trim() : 'No employment type provided';
            const deadline = deadlineElem ? deadlineElem.textContent.trim() : 'No deadline provided';
            const sector = sectorElem ? sectorElem.textContent.trim() : 'No sector provided';

            let postedDate = new Date();
            if (postedDateElem) {
                const postedDateText = postedDateElem.textContent.trim();
                const dateMatch = postedDateText.match(/(?:등록일|수정일)\s(\d{2})\/(\d{2})\/(\d{2})/);
                if (dateMatch) {
                    const [_, year, month, day] = dateMatch;
                    const formattedDate = `20${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`;
                    postedDate = new Date(formattedDate);
                }
            }

            return {
                title,
                company,
                link,
                location,
                experience,
                education,
                employmentType,
                deadline,
                sector,
                postedDate,
            };
        });
    });

    // 데이터 콘솔 출력
    if (jobItems.length === 0) {
        console.log('No job items found.');
    } else {
        console.log(`Found ${jobItems.length} job items:`);
        jobItems.forEach((job, idx) => {
            console.log(`${idx + 1}.`, job);
        });
    }

    await browser.close();
    console.log('Crawling completed.');
}

crawlJobs().catch(err => {
    console.error('Unhandled error:', err.message);
});
