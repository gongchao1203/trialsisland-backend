// API基础配置
const API_BASE_URL = 'http://localhost:8080';

// 全局状态
let currentUser = null;
let currentToken = null;
let currentPage = 'login';
let currentContest = null;
let currentQuestions = [];
let currentQuestionIndex = 0;
let contestStartTime = null;
let questionStartTime = null;

// 工具函数：显示Toast提示
function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type} show`;
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// 工具函数：发送API请求
async function apiRequest(url, options = {}) {
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };
    
    if (currentToken) {
        headers['Authorization'] = `Bearer ${currentToken}`;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}${url}`, {
            ...options,
            headers
        });
        
        const data = await response.json();
        
        if (!response.ok || data.code !== 200) {
            throw new Error(data.message || '请求失败');
        }
        
        return data.data;
    } catch (error) {
        showToast(error.message, 'error');
        throw error;
    }
}

// 页面切换
function showPage(pageName) {
    // 隐藏所有页面
    document.querySelectorAll('.page').forEach(page => {
        page.style.display = 'none';
    });
    
    // 显示目标页面
    const targetPage = document.getElementById(`${pageName}Page`);
    if (targetPage) {
        targetPage.style.display = 'block';
        currentPage = pageName;
        
        // 加载页面数据
        loadPageData(pageName);
    }
}

// 加载页面数据
async function loadPageData(pageName) {
    if (!currentToken && pageName !== 'login') {
        showPage('login');
        return;
    }
    
    switch (pageName) {
        case 'home':
            await loadHomeData();
            break;
        case 'contests':
            await loadContests();
            break;
        case 'vip':
            await loadVipStatus();
            break;
        case 'coin':
            await loadCoinData();
            break;
        case 'profile':
            await loadProfileData();
            break;
    }
}

// 登录处理
async function handleLogin(event) {
    event.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    
    try {
        const data = await apiRequest('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
        
        currentToken = data.token;
        currentUser = {
            id: data.userId,
            username: data.username,
            nickname: data.nickname
        };
        
        // 保存到localStorage
        localStorage.setItem('token', currentToken);
        localStorage.setItem('user', JSON.stringify(currentUser));
        
        // 更新导航栏
        updateNavbar();
        
        // 跳转到首页
        showPage('home');
        showToast('登录成功！');
    } catch (error) {
        console.error('登录失败:', error);
    }
}

// 退出登录
function logout() {
    currentToken = null;
    currentUser = null;
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    updateNavbar();
    showPage('login');
    showToast('已退出登录');
}

// 更新导航栏
function updateNavbar() {
    const navUser = document.getElementById('navUser');
    const navLogin = document.getElementById('navLogin');
    const userInfo = document.getElementById('userInfo');
    
    if (currentToken && currentUser) {
        navUser.style.display = 'flex';
        navLogin.style.display = 'none';
        userInfo.textContent = `欢迎，${currentUser.nickname || currentUser.username}`;
    } else {
        navUser.style.display = 'none';
        navLogin.style.display = 'flex';
    }
}

// 加载首页数据
async function loadHomeData() {
    try {
        // 获取金币账户信息
        const coinAccount = await apiRequest('/api/coin/account');
        document.getElementById('homeCoins').textContent = coinAccount.balance || 0;
        
        // 获取VIP状态
        const vipInfo = await apiRequest('/api/vip/info');
        const vipStatus = vipInfo && vipInfo.status === 1 ? 'VIP会员' : '未开通';
        document.getElementById('homeVipStatus').textContent = vipStatus;
        
        // 获取参赛记录
        const participations = await apiRequest('/api/contest/my-participations');
        document.getElementById('homeContests').textContent = participations.length || 0;
        
        // 计算最佳排名
        let bestRank = '-';
        if (participations && participations.length > 0) {
            const ranks = participations.map(p => p.rank).filter(r => r > 0);
            if (ranks.length > 0) {
                bestRank = Math.min(...ranks);
            }
        }
        document.getElementById('homeBestRank').textContent = bestRank;
    } catch (error) {
        console.error('加载首页数据失败:', error);
    }
}

// 加载比赛列表
async function loadContests() {
    try {
        const contests = await apiRequest('/api/contest/list');
        const contestsList = document.getElementById('contestsList');
        
        if (!contests || contests.length === 0) {
            contestsList.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">📝</div>
                    <div class="empty-state-text">暂无比赛</div>
                    <div class="empty-state-desc">敬请期待精彩比赛</div>
                </div>
            `;
            return;
        }
        
        contestsList.innerHTML = contests.map(contest => `
            <div class="contest-card">
                <div class="contest-header">
                    <div class="contest-name">${contest.name}</div>
                    <div class="contest-status ${getStatusClass(contest.status)}">
                        ${getStatusText(contest.status)}
                    </div>
                </div>
                <div class="contest-info">
                    <div>📝 题目数量：${contest.questionIds ? contest.questionIds.length : 0} 题</div>
                    <div>⏱️ 比赛时长：${contest.duration} 分钟</div>
                    <div>💰 报名费用：${contest.entryFee} 金币</div>
                    <div>👥 参赛人数：${contest.currentParticipants}/${contest.maxParticipants}</div>
                </div>
                <div class="contest-footer">
                    <div class="contest-prize">🏆 奖池：${contest.prizePool} 金币</div>
                    <button class="btn btn-primary" onclick="viewContestDetail(${contest.id})">
                        ${contest.status === 1 ? '立即参赛' : '查看详情'}
                    </button>
                </div>
            </div>
        `).join('');
    } catch (error) {
        console.error('加载比赛列表失败:', error);
    }
}

// 获取比赛状态样式类
function getStatusClass(status) {
    switch (status) {
        case 0: return 'status-pending';
        case 1: return 'status-ongoing';
        case 2: return 'status-finished';
        default: return '';
    }
}

// 获取比赛状态文本
function getStatusText(status) {
    switch (status) {
        case 0: return '未开始';
        case 1: return '进行中';
        case 2: return '已结束';
        default: return '未知';
    }
}

// 查看比赛详情
async function viewContestDetail(contestId) {
    try {
        const contest = await apiRequest(`/api/contest/${contestId}`);
        currentContest = contest;
        
        // 获取排行榜
        const leaderboard = await apiRequest(`/api/contest/${contestId}/ranking`);
        
        const contestDetail = document.getElementById('contestDetail');
        contestDetail.innerHTML = `
            <div class="contest-detail-card">
                <div class="contest-detail-header">
                    <h2>${contest.name}</h2>
                    <div class="contest-status ${getStatusClass(contest.status)}">
                        ${getStatusText(contest.status)}
                    </div>
                </div>
                
                <div class="contest-info">
                    <h3>比赛信息</h3>
                    <div>📝 题目数量：${contest.questionIds ? contest.questionIds.length : 0} 题</div>
                    <div>⏱️ 比赛时长：${contest.duration} 分钟</div>
                    <div>💰 报名费用：${contest.entryFee} 金币</div>
                    <div>🏆 奖池金额：${contest.prizePool} 金币</div>
                    <div>🥇 第一名：${contest.firstPrize} 金币</div>
                    <div>🥈 第二名：${contest.secondPrize} 金币</div>
                    <div>🥉 第三名：${contest.thirdPrize} 金币</div>
                    <div>👥 参赛人数：${contest.currentParticipants}/${contest.maxParticipants}</div>
                    <div>${contest.description || ''}</div>
                </div>
                
                ${contest.status === 1 ? `
                    <div style="text-align: center; margin: 30px 0;">
                        <button class="btn btn-primary btn-large" onclick="joinContest(${contest.id})">
                            立即报名参赛
                        </button>
                    </div>
                ` : ''}
                
                <div class="leaderboard">
                    <h3>排行榜</h3>
                    ${leaderboard && leaderboard.length > 0 ? leaderboard.map((item, index) => `
                        <div class="leaderboard-item">
                            <div class="leaderboard-rank rank-${item.rank}">
                                ${item.rank === 1 ? '🥇' : item.rank === 2 ? '🥈' : item.rank === 3 ? '🥉' : item.rank}
                            </div>
                            <div class="leaderboard-user">
                                <strong>${item.username}</strong>
                                <div style="font-size: 14px; color: #666;">
                                    正确：${item.correctCount}/${item.totalQuestions} 题 | 
                                    用时：${formatDuration(item.totalDuration)}
                                </div>
                            </div>
                            <div class="leaderboard-score">${item.totalScore} 分</div>
                        </div>
                    `).join('') : '<div class="empty-state-desc">暂无排名数据</div>'}
                </div>
                
                <div style="text-align: center; margin-top: 30px;">
                    <button class="btn" onclick="showPage('contests')">返回列表</button>
                </div>
            </div>
        `;
        
        showPage('contestDetail');
    } catch (error) {
        console.error('加载比赛详情失败:', error);
    }
}

// 报名参赛
async function joinContest(contestId) {
    try {
        await apiRequest('/api/contest/join', {
            method: 'POST',
            body: JSON.stringify({ contestId })
        });
        
        showToast('报名成功！');
        
        // 开始比赛
        await startContest(contestId);
    } catch (error) {
        console.error('报名失败:', error);
    }
}

// 开始比赛
async function startContest(contestId) {
    try {
        const data = await apiRequest(`/api/contest/${contestId}/start`, {
            method: 'POST'
        });
        
        currentQuestions = data.questions || [];
        currentQuestionIndex = 0;
        contestStartTime = Date.now();
        
        // 显示第一题
        showContestQuestion();
    } catch (error) {
        console.error('开始比赛失败:', error);
    }
}

// 显示比赛题目
function showContestQuestion() {
    if (currentQuestionIndex >= currentQuestions.length) {
        // 所有题目已完成
        finishContest();
        return;
    }
    
    const question = currentQuestions[currentQuestionIndex];
    questionStartTime = Date.now();
    
    const contestQuestion = document.getElementById('contestQuestion');
    contestQuestion.innerHTML = `
        <div class="question-container">
            <div class="question-header">
                <div class="question-progress">
                    题目 ${currentQuestionIndex + 1} / ${currentQuestions.length}
                </div>
                <div class="question-timer" id="questionTimer">00:00</div>
            </div>
            
            <div class="question-content">
                <div class="question-text">${question.question}</div>
                ${question.imageUrl ? `<img src="${question.imageUrl}" class="question-image" alt="题目图片">` : ''}
            </div>
            
            <div class="question-options">
                ${question.options ? question.options.map((option, index) => `
                    <div class="option-item" onclick="selectOption(${index})" id="option${index}">
                        ${String.fromCharCode(65 + index)}. ${option}
                    </div>
                `).join('') : ''}
            </div>
            
            <div class="question-actions">
                <button class="btn" onclick="showPage('contests')" ${currentQuestionIndex === 0 ? '' : 'disabled'}>
                    ${currentQuestionIndex === 0 ? '放弃比赛' : ''}
                </button>
                <button class="btn btn-primary" onclick="submitContestAnswer()" id="submitBtn" disabled>
                    ${currentQuestionIndex === currentQuestions.length - 1 ? '提交并完成' : '提交答案'}
                </button>
            </div>
        </div>
    `;
    
    showPage('contestQuestion');
    
    // 启动计时器
    startQuestionTimer();
}

let selectedOptionIndex = null;

// 选择选项
function selectOption(index) {
    // 清除之前的选择
    document.querySelectorAll('.option-item').forEach(item => {
        item.classList.remove('selected');
    });
    
    // 选中当前选项
    document.getElementById(`option${index}`).classList.add('selected');
    selectedOptionIndex = index;
    
    // 启用提交按钮
    document.getElementById('submitBtn').disabled = false;
}

// 启动题目计时器
function startQuestionTimer() {
    const timerElement = document.getElementById('questionTimer');
    const startTime = questionStartTime;
    
    const updateTimer = () => {
        const elapsed = Math.floor((Date.now() - startTime) / 1000);
        const minutes = Math.floor(elapsed / 60);
        const seconds = elapsed % 60;
        timerElement.textContent = `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
    };
    
    updateTimer();
    const intervalId = setInterval(updateTimer, 1000);
    
    // 保存intervalId以便清除
    timerElement.dataset.intervalId = intervalId;
}

// 提交比赛答案
async function submitContestAnswer() {
    if (selectedOptionIndex === null) {
        showToast('请选择一个选项', 'warning');
        return;
    }
    
    const question = currentQuestions[currentQuestionIndex];
    const answer = question.options[selectedOptionIndex];
    const duration = Math.floor((Date.now() - questionStartTime) / 1000);
    
    try {
        const result = await apiRequest('/api/contest/answer', {
            method: 'POST',
            body: JSON.stringify({
                contestId: currentContest.id,
                questionId: question.id,
                answer: answer,
                duration: duration
            })
        });
        
        // 显示答题结果
        showAnswerResult(result);
        
        // 清除计时器
        const timerElement = document.getElementById('questionTimer');
        if (timerElement && timerElement.dataset.intervalId) {
            clearInterval(parseInt(timerElement.dataset.intervalId));
        }
        
        // 延迟后显示下一题
        setTimeout(() => {
            currentQuestionIndex++;
            selectedOptionIndex = null;
            showContestQuestion();
        }, 2000);
    } catch (error) {
        console.error('提交答案失败:', error);
    }
}

// 显示答题结果
function showAnswerResult(result) {
    const options = document.querySelectorAll('.option-item');
    options.forEach((option, index) => {
        if (index === selectedOptionIndex) {
            option.classList.add(result.isCorrect ? 'correct' : 'wrong');
        }
        option.onclick = null; // 禁用点击
    });
    
    const message = result.isCorrect ? 
        `✅ 回答正确！得分：${result.score}` : 
        `❌ 回答错误。正确答案：${result.correctAnswer}`;
    
    showToast(message, result.isCorrect ? 'success' : 'error');
}

// 完成比赛
async function finishContest() {
    try {
        const result = await apiRequest(`/api/contest/${currentContest.id}/finish`, {
            method: 'POST'
        });
        
        // 显示比赛结果
        showContestResult(result);
    } catch (error) {
        console.error('完成比赛失败:', error);
    }
}

// 显示比赛结果
function showContestResult(result) {
    const contestResult = document.getElementById('contestResult');
    contestResult.innerHTML = `
        <div class="question-container" style="text-align: center;">
            <h2>🎉 比赛完成！</h2>
            
            <div class="stats-grid" style="margin: 40px 0;">
                <div class="stat-card">
                    <div class="stat-value">${result.rank || '-'}</div>
                    <div class="stat-label">排名</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">${result.totalScore}</div>
                    <div class="stat-label">总分</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">${result.correctCount}/${result.totalQuestions}</div>
                    <div class="stat-label">正确题数</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">${formatDuration(result.totalDuration)}</div>
                    <div class="stat-label">总用时</div>
                </div>
            </div>
            
            ${result.rewardCoins > 0 ? `
                <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 16px; margin: 30px 0;">
                    <h3 style="margin: 0 0 10px 0;">🏆 恭喜获得奖励！</h3>
                    <div style="font-size: 36px; font-weight: bold;">${result.rewardCoins} 金币</div>
                </div>
            ` : ''}
            
            <div style="margin-top: 40px;">
                <button class="btn btn-primary" onclick="viewContestDetail(${currentContest.id})" style="margin-right: 10px;">
                    查看排行榜
                </button>
                <button class="btn" onclick="showPage('contests')">
                    返回比赛列表
                </button>
            </div>
        </div>
    `;
    
    showPage('contestResult');
}

// 格式化时长
function formatDuration(seconds) {
    const minutes = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${minutes}分${secs}秒`;
}

// 加载VIP状态
async function loadVipStatus() {
    try {
        const vipInfo = await apiRequest('/api/vip/info');
        const coinAccount = await apiRequest('/api/coin/account');
        
        document.getElementById('currentVipStatus').textContent = 
            vipInfo && vipInfo.status === 1 ? 'VIP会员' : '未开通';
        document.getElementById('vipCoins').textContent = coinAccount.balance || 0;
        
        if (vipInfo && vipInfo.status === 1) {
            const expireDate = new Date(vipInfo.expireTime).toLocaleDateString();
            document.getElementById('vipExpireTime').textContent = `到期时间：${expireDate}`;
        } else {
            document.getElementById('vipExpireTime').textContent = '';
        }
    } catch (error) {
        console.error('加载VIP状态失败:', error);
    }
}

// 购买VIP
async function purchaseVip(level) {
    const levelNames = ['', '月卡', '季卡', '年卡'];
    const prices = [0, 100, 270, 960];
    
    if (!confirm(`确认购买${levelNames[level]}（${prices[level]}金币）？`)) {
        return;
    }
    
    try {
        await apiRequest('/api/vip/purchase', {
            method: 'POST',
            body: JSON.stringify({ level })
        });
        
        showToast(`成功购买${levelNames[level]}！`);
        await loadVipStatus();
    } catch (error) {
        console.error('购买VIP失败:', error);
    }
}

// 加载金币数据
async function loadCoinData() {
    try {
        const account = await apiRequest('/api/coin/account');
        const transactions = await apiRequest('/api/coin/transactions');
        
        document.getElementById('coinBalance').textContent = account.balance || 0;
        document.getElementById('coinTotalRecharge').textContent = account.totalRecharge || 0;
        document.getElementById('coinTotalSpend').textContent = account.totalSpend || 0;
        document.getElementById('coinTotalReward').textContent = account.totalReward || 0;
        
        // 显示交易记录
        const transactionList = document.getElementById('transactionList');
        if (!transactions || transactions.length === 0) {
            transactionList.innerHTML = '<div class="empty-state-desc">暂无交易记录</div>';
        } else {
            transactionList.innerHTML = transactions.slice(0, 10).map(tx => `
                <div class="transaction-item">
                    <div>
                        <div><strong>${getTransactionTypeText(tx.type)}</strong></div>
                        <div style="font-size: 14px; color: #666;">${new Date(tx.createTime).toLocaleString()}</div>
                        ${tx.description ? `<div style="font-size: 12px; color: #999;">${tx.description}</div>` : ''}
                    </div>
                    <div class="transaction-amount ${tx.amount > 0 ? 'positive' : 'negative'}">
                        ${tx.amount > 0 ? '+' : ''}${tx.amount}
                    </div>
                </div>
            `).join('');
        }
    } catch (error) {
        console.error('加载金币数据失败:', error);
    }
}

// 获取交易类型文本
function getTransactionTypeText(type) {
    const types = {
        1: '💰 充值',
        2: '💸 消费',
        3: '🎁 奖励',
        4: '💵 提现'
    };
    return types[type] || '未知';
}

// 充值金币
async function rechargeCoins() {
    const amount = prompt('请输入充值金额（最低100金币）：');
    if (!amount || isNaN(amount) || parseInt(amount) < 100) {
        showToast('请输入有效的充值金额（最低100金币）', 'warning');
        return;
    }
    
    try {
        await apiRequest('/api/coin/recharge', {
            method: 'POST',
            body: JSON.stringify({ 
                amount: parseInt(amount),
                description: '用户充值'
            })
        });
        
        showToast(`成功充值${amount}金币！`);
        await loadCoinData();
    } catch (error) {
        console.error('充值失败:', error);
    }
}

// 提现
async function withdrawCoins() {
    const coinAmount = prompt('请输入提现金币数量（100金币=1元，必须是100的整数倍）：');
    if (!coinAmount || isNaN(coinAmount) || parseInt(coinAmount) < 100 || parseInt(coinAmount) % 100 !== 0) {
        showToast('请输入有效的提现金币数量（必须≥100且是100的整数倍）', 'warning');
        return;
    }
    
    const cashAmount = parseInt(coinAmount) / 100;
    if (!confirm(`确认提现${coinAmount}金币（${cashAmount}元）？`)) {
        return;
    }
    
    try {
        await apiRequest('/api/coin/withdraw', {
            method: 'POST',
            body: JSON.stringify({ coinAmount: parseInt(coinAmount) })
        });
        
        showToast(`成功提现${cashAmount}元！`);
        await loadCoinData();
    } catch (error) {
        console.error('提现失败:', error);
    }
}

// 加载个人中心数据
async function loadProfileData() {
    try {
        const participations = await apiRequest('/api/contest/my-participations');
        const coinAccount = await apiRequest('/api/coin/account');
        const vipInfo = await apiRequest('/api/vip/info');
        
        document.getElementById('profileUsername').textContent = currentUser.username;
        document.getElementById('profileCoins').textContent = coinAccount.balance || 0;
        document.getElementById('profileVipStatus').textContent = 
            vipInfo && vipInfo.status === 1 ? 'VIP会员' : '未开通';
        document.getElementById('profileContests').textContent = participations.length || 0;
        
        // 显示参赛历史
        const historyList = document.getElementById('contestHistory');
        if (!participations || participations.length === 0) {
            historyList.innerHTML = '<div class="empty-state-desc">暂无参赛记录</div>';
        } else {
            historyList.innerHTML = participations.map(p => `
                <div class="contest-card" style="cursor: pointer;" onclick="viewContestDetail(${p.contestId})">
                    <div class="contest-header">
                        <h3>比赛 #${p.contestId}</h3>
                        <span class="contest-status ${p.status === 2 ? 'ongoing' : 'completed'}">
                            ${p.status === 2 ? '进行中' : '已完成'}
                        </span>
                    </div>
                    <div class="stats-grid" style="margin-top: 15px;">
                        <div class="stat-card">
                            <div class="stat-value">${p.rank || '-'}</div>
                            <div class="stat-label">排名</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-value">${p.totalScore}</div>
                            <div class="stat-label">得分</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-value">${p.correctCount}/${p.totalQuestions}</div>
                            <div class="stat-label">正确率</div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-value">${p.rewardCoins > 0 ? '+' + p.rewardCoins : '0'}</div>
                            <div class="stat-label">奖励</div>
                        </div>
                    </div>
                    <div class="contest-footer" style="margin-top: 15px; color: #666; font-size: 14px;">
                        参赛时间：${new Date(p.registrationTime).toLocaleString()}
                    </div>
                </div>
            `).join('');
        }
    } catch (error) {
        console.error('加载个人中心数据失败:', error);
    }
}

// 登出
function logout() {
    if (confirm('确认退出登录？')) {
        localStorage.removeItem('token');
        currentUser = null;
        showPage('login');
        showToast('已退出登录');
    }
}

// Toast提示
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.classList.add('show');
    }, 100);
    
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => {
            document.body.removeChild(toast);
        }, 300);
    }, 3000);
}

// 初始化
document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    if (token) {
        // 尝试自动登录
        apiRequest('/api/auth/verify').then(user => {
            currentUser = user;
            showPage('home');
        }).catch(() => {
            localStorage.removeItem('token');
            showPage('login');
        });
    } else {
        showPage('login');
    }
});