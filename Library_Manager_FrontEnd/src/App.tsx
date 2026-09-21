import { useState } from 'react';
import Navbar, { type Page } from './components/Navbar';
import UsersPage from './pages/UsersPage';
import BooksPage from './pages/BooksPage';
import LoansPage from './pages/LoansPage';

function App() {
  const [page, setPage] = useState<Page>('users');

  return (
    <div className="app">
      <Navbar current={page} onChange={setPage} />
      <main className="container">
        {page === 'users' && <UsersPage />}
        {page === 'books' && <BooksPage />}
        {page === 'loans' && <LoansPage />}
      </main>
    </div>
  );
}

export default App;
